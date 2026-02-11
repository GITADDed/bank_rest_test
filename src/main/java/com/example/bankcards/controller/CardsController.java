package com.example.bankcards.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/api/v1")
public class CardsController {

    @GetMapping("/cards")
    String getCards() {
        return "cards";
    }
}
