package com.example.bankcards.dto;

import com.example.bankcards.entity.CardStatus;

import java.math.BigDecimal;

public record CardResponse(Long id, String maskedPan, String last4, Integer expiryMonth, Integer expiryYear, CardStatus status, BigDecimal balance) {
}
