package com.example.bankcards.service;

import com.example.bankcards.dto.CardRequest;
import com.example.bankcards.dto.CardResponse;

public interface CreateCardService {
    CardResponse createCard(CardRequest request);
}
