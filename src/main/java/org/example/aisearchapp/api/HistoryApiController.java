package org.example.aisearchapp.api;

import org.example.aisearchapp.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/history")
public class HistoryApiController {

    private final SearchService searchService;

    @Autowired
    public HistoryApiController(SearchService searchService) {
        this.searchService = searchService;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHistoryItem(@PathVariable Long id) {
        searchService.deleteHistoryItemById(id);
        return ResponseEntity.ok().build();
    }


    @DeleteMapping
    public ResponseEntity<Void> clearAllHistoryForUser(@AuthenticationPrincipal OAuth2User principal) {
        if (principal == null) {
            return ResponseEntity.status(401).build();
        }
        String email = principal.getAttribute("email");
        searchService.clearHistoryForUser(email);
        return ResponseEntity.ok().build();
    }
}
