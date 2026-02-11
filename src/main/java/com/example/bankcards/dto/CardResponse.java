package com.example.bankcards.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record CardResponse(UUID id, String maskedPan, String last4, Integer expiryMonth, Integer expiryYear, CardStatus status, BigDecimal balance) {
}
