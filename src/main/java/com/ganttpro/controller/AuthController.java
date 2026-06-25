package com.ganttpro.controller;

import com.ganttpro.dto.RegisterRequest;
import com.ganttpro.service.AuthService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/")
    public String index() {
        return "redirect:/projects/1";
    }

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("registerRequest", new RegisterRequest());
        return "auth/register";
    }

    @PostMapping("/register")
    public String registerSubmit(RegisterRequest request, Model model) {
        if (!authService.register(request)) {
            model.addAttribute("error", "Пользователь с этим email уже существует или пароли не совпадают");
            return "auth/register";
        }
        return "redirect:/login?success=Регистрация успешна. Пожалуйста, войдите.";
    }
}
