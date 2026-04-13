package org.example.aisearchapp.api;

import org.example.aisearchapp.dto.SearchResponse;
import org.example.aisearchapp.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@RestController
@RequestMapping("/api")
public class SearchApiController {

    private final SearchService searchService;

    @Autowired
    public SearchApiController(SearchService searchService) {
        this.searchService = searchService;
    }

    @PostMapping("/search")
    public SearchResponse handleSearch(@RequestBody Map<String, String> payload, @AuthenticationPrincipal OAuth2User principal) {
        if (principal == null) {
            throw new ResponseStatusException(UNAUTHORIZED, "User not authenticated.");
        }

        String query = payload.get("query");
        if (query == null || query.trim().isEmpty()) {
            throw new ResponseStatusException(BAD_REQUEST, "Query is required.");
        }

        String userEmail = principal.getAttribute("email");
        if (userEmail == null || userEmail.trim().isEmpty()) {
            throw new ResponseStatusException(UNAUTHORIZED, "User email not available.");
        }

        return searchService.getAiSummary(query, userEmail);
    }
}