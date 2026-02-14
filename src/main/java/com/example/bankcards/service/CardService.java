package com.example.bankcards.service;

import com.example.bankcards.dto.CardRequest;
import com.example.bankcards.dto.CardResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CardService {
    CardResponse createCard(CardRequest request);
    Page<CardResponse> getAllCards(Pageable pageable);
    CardResponse getCardById(Long id);
}
