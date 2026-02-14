package com.example.bankcards.validation.rules;

import com.example.bankcards.util.Violation;

import java.util.Optional;

// For @Order. 0 - uses for null checks rules, 1 - for necessary value checks, 2 - for business logic checks, 3 - for format checks
public interface ValidationRule<T> {
    Optional<Violation> applyRule(T request);

    default boolean stopOnFailure() {
        return false;
    }
}
