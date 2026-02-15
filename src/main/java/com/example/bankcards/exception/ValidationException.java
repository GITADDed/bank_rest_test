package com.example.bankcards.exception;

import com.example.bankcards.entity.Violation;
import lombok.Getter;

import java.util.List;

@Getter
public class ValidationException extends BaseException {
    public ValidationException(List<Violation> violations) {
        super("VALIDATION_ERROR", violations, "Validation failed for one or more fields.");
    }
}
