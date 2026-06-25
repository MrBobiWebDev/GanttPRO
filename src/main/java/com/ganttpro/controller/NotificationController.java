package com.ganttpro.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/notifications")
public class NotificationController {

    @GetMapping
    public String notifications(Authentication authentication) {
        if (authentication == null) {
            return "redirect:/login";
        }
        return "notifications";
    }
}
