package com.example.bankcards.validation.rules;

import com.example.bankcards.util.Violation;

import java.util.Optional;

public interface ValidationRule<T> {
    Optional<Violation> applyRule(T request);
}
