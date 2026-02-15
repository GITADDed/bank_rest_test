package com.example.bankcards.dto;

import jakarta.validation.constraints.*;

public record CardRequest(
        @NotNull(message = "Owner ID cannot be null.")
        @Min(value = 1, message = "Owner ID must be a positive number.")
        Long ownerId,

        @NotBlank(message = "PAN must not be blank.")
        String pan,

        @NotNull(message = "Expiry month cannot be null.")
        @Min(value = 1, message = "Expiry month must be between 1 and 12.")
        @Max(value = 12, message = "Expiry month must be between 1 and 12.")
        Integer expiryMonth,

        @NotNull(message = "Expiry year cannot be null.")
        Integer expiryYear) { }
