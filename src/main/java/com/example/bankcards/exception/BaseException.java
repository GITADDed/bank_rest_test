package com.example.bankcards.exception;

import com.example.bankcards.entity.Violation;
import lombok.Getter;

import java.util.List;

@Getter
public abstract class BaseException extends RuntimeException {
    private final String code;
    private final List<Violation> violations;

    public BaseException(String code, List<Violation> violations, String message) {
        super(message);
        this.code = code;
        this.violations = violations;
    }
}
