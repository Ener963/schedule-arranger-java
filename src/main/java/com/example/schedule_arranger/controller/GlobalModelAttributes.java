package com.example.schedule_arranger.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAttributes {

    @ModelAttribute("githubLogin")
    public String githubLogin(@AuthenticationPrincipal OAuth2User principal) {
        return principal != null ? principal.getAttribute("login") : null;
    }
}
