package org.example.aisearchapp.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.aisearchapp.dto.SearchResponse;
import org.example.aisearchapp.model.SearchHistory;
import org.example.aisearchapp.model.User;
import org.example.aisearchapp.repository.SearchHistoryRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class SearchService {

    @Value("${google.search.api.key}")
    private String googleApiKey;

    @Value("${google.search.engine.id}")
    private String searchEngineId;

    @Value("${hf.space.base-url}")
    private String hfSpaceBaseUrl;

    @Value("${hf.space.api-key:}")
    private String hfSpaceApiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final SearchHistoryRepository searchHistoryRepository;
    private final UserService userService;

    @Autowired
    public SearchService(SearchHistoryRepository searchHistoryRepository, UserService userService) {
        this.searchHistoryRepository = searchHistoryRepository;
        this.userService = userService;
    }

    @Transactional
    public SearchResponse getAiSummary(String query, String userEmail) {
        String normalizedQuery = normalizeQuery(query);
        if (!StringUtils.hasText(normalizedQuery)) {
            return new SearchResponse("Please enter a valid search query.", List.of(), List.of(), List.of());
        }

        User user = userService.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found: " + userEmail));

        List<String> links = searchGoogle(normalizedQuery);
        if (links.isEmpty()) {
            return new SearchResponse("No relevant information found.", List.of(), List.of(), List.of());
        }

        List<String> scrapedContents = scrapeLinks(links);

        SearchResponse response = summarizeWithHfSpace(normalizedQuery, scrapedContents, links);

        if (!StringUtils.hasText(response.getAnswer())) {
            response.setAnswer("I could not generate a summary. Most sources are currently inaccessible.");
        }

        List<String> followUpQuestions = generateFollowUpQuestions(
                normalizedQuery,
                response.getAnswer(),
                response.getKeyPoints()
        );
        response.setFollowUpQuestions(followUpQuestions);

        SearchHistory history = new SearchHistory(normalizedQuery, response.getAnswer(), LocalDateTime.now(), user);
        searchHistoryRepository.save(history);

        return response;
    }

    private String normalizeQuery(String query) {
        if (query == null) {
            return "";
        }

        String normalized = query.trim();
        normalized = normalized.replaceAll("[\"']", "");
        normalized = normalized.replaceAll("\\s+", " ");
        normalized = normalized.replaceAll("\\bbranchers\\b", "branches");
        normalized = normalized.replaceAll("\\bbranchs\\b", "branches");

        if (normalized.toLowerCase().startsWith("where ")) {
            normalized = normalized + " locations";
        }

        return normalized.trim();
    }

    private List<String> searchGoogle(String query) {
        List<String> links = new ArrayList<>();

        try {
            String url = UriComponentsBuilder.fromHttpUrl("https://www.googleapis.com/customsearch/v1")
                    .queryParam("key", googleApiKey)
                    .queryParam("cx", searchEngineId)
                    .queryParam("q", query)
                    .build()
                    .toUriString();

            String response = restTemplate.getForObject(url, String.class);
            JsonNode root = objectMapper.readTree(response);
            JsonNode items = root.path("items");

            if (items.isArray()) {
                Set<String> uniqueLinks = new LinkedHashSet<>();

                for (JsonNode item : items) {
                    String link = item.path("link").asText("");
                    if (!StringUtils.hasText(link)) {
                        continue;
                    }
                    if (isBlockedSource(link, query)) {
                        continue;
                    }
                    uniqueLinks.add(link);
                    if (uniqueLinks.size() >= 5) {
                        break;
                    }
                }

                links.addAll(uniqueLinks);
            }
        } catch (Exception e) {
            System.err.println("Google Search Error: " + e.getMessage());
        }

        return links;
    }

    private boolean isBlockedSource(String link, String query) {
        String lowerLink = link.toLowerCase();
        String lowerQuery = query == null ? "" : query.toLowerCase();

        if (lowerLink.contains("reddit.com") || lowerLink.contains("quora.com")) {
            return true;
        }

        if (lowerLink.contains("facebook.com") || lowerLink.contains("instagram.com") || lowerLink.contains("x.com") || lowerLink.contains("twitter.com")) {
            return true;
        }

        if (lowerQuery.contains("nibm")) {
            if (lowerLink.contains("axis.bank")
                    || lowerLink.contains("hdfc.bank")
                    || lowerLink.contains("centralbank.bank")
                    || lowerLink.contains("au.bank")
                    || lowerLink.contains("bank.")) {
                return true;
            }
        }

        return false;
    }

    private List<String> scrapeLinks(List<String> links) {
        List<String> contents = new ArrayList<>();
        String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/123.0.0.0 Safari/537.36";

        for (String link : links) {
            try {
                Document doc = Jsoup.connect(link)
                        .userAgent(userAgent)
                        .referrer("https://www.google.com/")
                        .header("Accept-Language", "en-US,en;q=0.9")
                        .timeout(12000)
                        .get();

                String text = doc.body().text().replaceAll("\\s+", " ").trim();
                if (text.length() > 4000) {
                    text = text.substring(0, 4000);
                }
                if (StringUtils.hasText(text)) {
                    contents.add(text);
                }
            } catch (IOException e) {
                System.err.println("Scraping Error: " + link + " | " + e.getMessage());
            }
        }

        return contents;
    }

    private SearchResponse summarizeWithHfSpace(String query, List<String> contents, List<String> links) {
        try {
            String endpoint = normalizeBaseUrl(hfSpaceBaseUrl) + "/summarize-search";

            Map<String, Object> payload = new HashMap<>();
            payload.put("query", query);
            payload.put("contents", contents.isEmpty() ? List.of("No direct content available.") : contents);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, buildHeaders());
            String response = restTemplate.postForObject(endpoint, entity, String.class);
            JsonNode root = objectMapper.readTree(response);

            return new SearchResponse(
                    root.path("answer").asText("").trim(),
                    readStringArray(root.path("key_points")),
                    links,
                    List.of()
            );
        } catch (Exception e) {
            return new SearchResponse("Error generating answer.", List.of(), links, List.of());
        }
    }

    private List<String> generateFollowUpQuestions(String query, String answer, List<String> points) {
        try {
            String endpoint = normalizeBaseUrl(hfSpaceBaseUrl) + "/generate";

            StringBuilder promptBuilder = new StringBuilder();
            promptBuilder.append("Suggest 3 natural follow-up questions a user may ask next.\n");
            promptBuilder.append("The questions must continue the topic in a useful way.\n");
            promptBuilder.append("Do not convert the existing key points directly into questions.\n\n");
            promptBuilder.append("Original Query:\n").append(query).append("\n\n");
            promptBuilder.append("Answer:\n").append(answer).append("\n\n");

            if (points != null && !points.isEmpty()) {
                promptBuilder.append("Key Points:\n");
                for (String p : points) {
                    promptBuilder.append("- ").append(p).append("\n");
                }
            }

            Map<String, Object> payload = new HashMap<>();
            payload.put("prompt", promptBuilder.toString());
            payload.put("max_tokens", 220);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, buildHeaders());
            String response = restTemplate.postForObject(endpoint, entity, String.class);

            JsonNode root = objectMapper.readTree(response);

            List<String> raw = readStringArray(root.path("key_points"));
            List<String> questions = new ArrayList<>();

            for (String value : raw) {
                String cleaned = value == null ? "" : value.trim();
                if (!StringUtils.hasText(cleaned)) {
                    continue;
                }
                if (!cleaned.endsWith("?")) {
                    cleaned = cleaned + "?";
                }
                questions.add(cleaned);
                if (questions.size() == 3) {
                    break;
                }
            }

            return questions;
        } catch (Exception e) {
            System.err.println("Follow-up error: " + e.getMessage());
            return List.of();
        }
    }

    private HttpHeaders buildHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (StringUtils.hasText(hfSpaceApiKey)) {
            headers.set("X-API-Key", hfSpaceApiKey);
        }
        return headers;
    }

    private String normalizeBaseUrl(String baseUrl) {
        if (baseUrl != null && baseUrl.endsWith("/")) {
            return baseUrl.substring(0, baseUrl.length() - 1);
        }
        return baseUrl;
    }

    private List<String> readStringArray(JsonNode node) {
        List<String> values = new ArrayList<>();
        if (node.isArray()) {
            for (JsonNode item : node) {
                if (StringUtils.hasText(item.asText())) {
                    values.add(item.asText().trim());
                }
            }
        }
        return values;
    }

    public List<SearchHistory> getSearchHistoryForUser(String email) {
        User user = userService.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        return searchHistoryRepository.findByUserOrderBySearchTimestampDesc(user);
    }

    @Transactional
    public void deleteHistoryItemById(Long id) {
        searchHistoryRepository.deleteById(id);
    }

    @Transactional
    public void clearHistoryForUser(String email) {
        User user = userService.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        searchHistoryRepository.deleteByUser(user);
    }
}