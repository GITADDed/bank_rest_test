package com.example.bankcards.dto;

public record CardRequest(Long ownerId, String pan, Integer expiryMonth, Integer expiryYear) {
}
