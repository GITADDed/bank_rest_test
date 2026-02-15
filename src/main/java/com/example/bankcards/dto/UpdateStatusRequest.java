package com.example.bankcards.dto;

import com.example.bankcards.entity.CardStatus;

public record UpdateStatusRequest(CardStatus status) {
}
