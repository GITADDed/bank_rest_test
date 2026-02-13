package com.example.bankcards.exception;

import com.example.bankcards.util.Violation;
import lombok.Getter;

import java.util.List;

@Getter
public class NotFoundException extends RuntimeException {
    private final String code = "NOT_FOUND_ERROR";
    private final List<Violation> violations;

    public NotFoundException(List<Violation> violations) {
        super("Not found some resource.");
        this.violations = violations;
    }
}
