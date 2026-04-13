package org.example.aisearchapp.api;

import org.example.aisearchapp.service.SuggestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class SuggestionApiController {

    private final SuggestionService suggestionService;

    @Autowired
    public SuggestionApiController(SuggestionService suggestionService) {
        this.suggestionService = suggestionService;
    }

    @GetMapping("/suggestions")
    public List<String> getSuggestions(@RequestParam("q") String query) {
        return suggestionService.getSuggestions(query);
    }
}
