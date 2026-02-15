package com.example.bankcards.validation.rules.business;

import com.example.bankcards.entity.TransferViolation;

import java.util.Optional;

public interface BusinessRule<T> {
    Optional<TransferViolation> applyRule(T data);

    default boolean stopOnFailure() {
        return false;
    }
}
