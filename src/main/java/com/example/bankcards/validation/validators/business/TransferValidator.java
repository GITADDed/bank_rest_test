package com.example.bankcards.validation.validators.business;

import com.example.bankcards.entity.TransferCheckResult;
import com.example.bankcards.entity.TransferData;

public interface TransferValidator {
        TransferCheckResult validate(TransferData data);
}
