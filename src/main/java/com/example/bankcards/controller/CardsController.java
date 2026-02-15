package com.example.bankcards.controller;

import com.example.bankcards.dto.CardRequest;
import com.example.bankcards.dto.CardResponse;
import com.example.bankcards.dto.UpdateStatusRequest;
import com.example.bankcards.dto.PageResponse;
import com.example.bankcards.exception.UnauthorizedException;
import com.example.bankcards.service.CardService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
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
    PageResponse<CardResponse> getMyAllCards(@AuthenticationPrincipal Jwt jwt,
                                             @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC)
            Pageable pageable) {
        Long userId = jwt.getClaim("uid");

        if (userId == null)
            throw new UnauthorizedException();

        Page<CardResponse> page = cardService.getMyAllCards(userId, pageable);

        return new PageResponse<>(page.getContent(), page.getNumber(), page.getSize(),
                page.getTotalElements(), page.getTotalPages());
    }

    @GetMapping("/cards/{id}")
    CardResponse getCardById(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        Long userId = jwt.getClaim("uid");

        if (userId == null)
            throw new UnauthorizedException();

        return cardService.getCardById(id, userId);
    }

    @GetMapping("/admin/cards/{id}")
    CardResponse getCardByIdForAdmin(@PathVariable Long id) {
        return cardService.getCardById(id);
    }

    @PostMapping("/admin/cards")
    CardResponse createCard(@Valid @RequestBody CardRequest request) {
        return cardService.createCard(request);
    }

    @PatchMapping("/admin/cards/{id}/status")
    CardResponse updateCardStatus(@PathVariable Long id, @Valid @RequestBody UpdateStatusRequest request) {
        return cardService.updateCardStatus(id, request.status());
    }

    @DeleteMapping("/admin/cards/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    void deleteCard(@PathVariable Long id) {
        cardService.deleteCard(id);
    }
}
