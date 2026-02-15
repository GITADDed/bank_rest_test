package com.example.bankcards.entity;

import com.example.bankcards.dto.TransferRequest;

public record TransferData(Card from, Card to, TransferRequest request) {
}
