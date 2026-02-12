package com.example.bankcards.controller;

import com.example.bankcards.dto.CardRequest;
import com.example.bankcards.dto.CardResponse;
import com.example.bankcards.service.CreateCardService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@RestController
@RequestMapping("/api/v1")
public class CardsController {

    private final CreateCardService createCardService;

    @GetMapping("/cards")
    String getCards() {
        return "cards";
    }

    @PostMapping("/cards")
    CardResponse createCard(@RequestBody CardRequest request) {
        return createCardService.createCard(request);
    }
}
