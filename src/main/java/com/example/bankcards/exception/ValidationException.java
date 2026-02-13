package com.example.bankcards.exception;

import com.example.bankcards.util.Violation;
import lombok.Getter;

import java.util.List;

@Getter
public class ValidationException extends RuntimeException {
    private final String code;
    private final List<Violation> violations;

    public ValidationException(List<Violation> violations) {
        super("Validation failed for one or more fields.");
        this.code = "VALIDATION_ERROR";
        this.violations = violations;
    }
}
