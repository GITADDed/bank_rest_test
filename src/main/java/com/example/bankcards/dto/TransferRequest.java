package com.example.bankcards.dto;


import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record TransferRequest(
        @NotNull(message = "Source card ID cannot be null.")
        Long fromCardId,

        @NotNull(message = "Destination card ID cannot be null.")
        Long toCardId,

        @NotNull(message = "Amount cannot be null")
        @DecimalMin(value = "0.01", message = "Amount must be greater than zero.")
        BigDecimal amount) {
}
