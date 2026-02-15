package com.example.bankcards.controller;

import com.example.bankcards.dto.CardRequest;
import com.example.bankcards.dto.CardResponse;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.dto.PageResponse;
import com.example.bankcards.service.CardService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@RestController
public class CardsController {

    private final CardService cardService;

    @GetMapping("/admin/cards")
    PageResponse<CardResponse> getAllCards(
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC)
            Pageable pageable) {
        Page<CardResponse> page = cardService.getAllCards(pageable);

        return new PageResponse<>(page.getContent(), page.getNumber(), page.getSize(),
                page.getTotalElements(), page.getTotalPages());
    }

    @GetMapping("/cards")
    PageResponse<CardResponse> getMyAllCards(@RequestHeader("X-User-Id") Long userId,
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC)
            Pageable pageable) {
        Page<CardResponse> page = cardService.getMyAllCards(userId, pageable);

        return new PageResponse<>(page.getContent(), page.getNumber(), page.getSize(),
                page.getTotalElements(), page.getTotalPages());
    }

    @GetMapping("/cards/{id}")
    CardResponse getCardById(@PathVariable Long id) {
        return cardService.getCardById(id);
    }

    @PostMapping("/admin/cards")
    CardResponse createCard(@RequestBody CardRequest request) {
        return cardService.createCard(request);
    }

    @PatchMapping("/admin/cards/{id}/status")
    CardResponse updateCardStatus(@PathVariable Long id, @RequestBody CardStatus status) {
        return cardService.updateCardStatus(id, status);
    }

    @DeleteMapping("/admin/cards/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    void deleteCard(@PathVariable Long id) {
        cardService.deleteCard(id);
    }
}
