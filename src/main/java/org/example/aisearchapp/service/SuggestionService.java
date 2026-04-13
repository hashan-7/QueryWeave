package org.example.aisearchapp.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

@Service
public class SuggestionService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<String> getSuggestions(String query) {
        String url = "http://suggestqueries.google.com/complete/search?client=firefox&q=" + query;
        List<String> suggestions = new ArrayList<>();

        try {
            String response = restTemplate.getForObject(url, String.class);
            JsonNode root = objectMapper.readTree(response);
            JsonNode suggestionsNode = root.get(1);

            if (suggestionsNode != null && suggestionsNode.isArray()) {
                for (JsonNode node : suggestionsNode) {
                    suggestions.add(node.asText());
                }
            }
        } catch (Exception e) {
            System.err.println("Error fetching suggestions: " + e.getMessage());

        }

        return suggestions;
    }
}
