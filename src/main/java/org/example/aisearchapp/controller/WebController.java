package org.example.aisearchapp.controller;

import org.example.aisearchapp.model.SearchHistory;
import org.example.aisearchapp.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class WebController {

    private final SearchService searchService;

    @Autowired
    public WebController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping("/")
    public String home() {
        return "index";
    }

    private void addUserAttributesToModel(Model model, OAuth2User principal) {
        if (principal != null) {
            String name = principal.getAttribute("name");
            String picture = principal.getAttribute("picture");
            String email = principal.getAttribute("email");
            model.addAttribute("userName", name);
            model.addAttribute("userPicture", picture);
            model.addAttribute("userEmail", email);
        }
    }

    @GetMapping("/search")
    public String search(Model model, @AuthenticationPrincipal OAuth2User principal) {
        addUserAttributesToModel(model, principal);
        return "search";
    }

    @GetMapping("/history")
    public String history(Model model, @AuthenticationPrincipal OAuth2User principal) {
        addUserAttributesToModel(model, principal);
        String userEmail = principal.getAttribute("email");
        List<SearchHistory> historyItems = searchService.getSearchHistoryForUser(userEmail);
        model.addAttribute("historyItems", historyItems);
        return "history";
    }

    @GetMapping("/profile")
    public String profile(Model model, @AuthenticationPrincipal OAuth2User principal) {
        addUserAttributesToModel(model, principal);
        return "profile";
    }
}
