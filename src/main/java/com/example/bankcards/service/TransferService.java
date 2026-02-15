package com.example.bankcards.service;

import com.example.bankcards.dto.TransferRequest;
import com.example.bankcards.dto.TransferResponse;

public interface TransferService {
    TransferResponse transfer(TransferRequest request);
}
