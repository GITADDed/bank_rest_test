package com.example.bankcards.service;

import com.example.bankcards.entity.Card;

public interface CardAccessService {
    Card getOwnedCardOrThrow(Long cardId, Long userId, String field);
}
