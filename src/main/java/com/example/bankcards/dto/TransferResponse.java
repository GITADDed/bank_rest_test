package com.example.bankcards.dto;

import com.example.bankcards.entity.TransferStatus;

public record TransferResponse(Long id, Long fromCardId, Long toCardId, TransferStatus status) {
}
