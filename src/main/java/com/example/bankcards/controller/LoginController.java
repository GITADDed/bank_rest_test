package com.example.bankcards.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/api/v1")
public class LoginController {

    @PostMapping("/auth/login")
    String login() {
        return "login";
    }
}
