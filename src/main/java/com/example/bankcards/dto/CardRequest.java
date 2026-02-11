package com.example.bankcards.dto;

import java.util.UUID;

public record CardRequest(UUID ownerId, Integer expiryMonth, Integer expiryYear) {
}
