package com.example.schedule_arranger.controller;

import com.example.schedule_arranger.repository.ScheduleRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final ScheduleRepository scheduleRepository;

    public HomeController(ScheduleRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
    }

    @GetMapping("/")
    public String index(@AuthenticationPrincipal OAuth2User principal, Model model) {
        if (principal != null) {
            Integer userId = ((Number) principal.getAttribute("id")).intValue();
            model.addAttribute("schedules", scheduleRepository.findByCreatedByOrderByUpdatedAtDesc(userId));
        }
        return "index";
    }
}