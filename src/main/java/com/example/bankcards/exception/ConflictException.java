package com.example.bankcards.exception;

import com.example.bankcards.entity.Violation;

import java.util.List;

public class ConflictException extends BaseException {
    public ConflictException(List<Violation> violations) {
        super("CONFLICT_ERROR", violations, "Conflict occurred.");
    }

    public ConflictException(List<Violation> violations, String message, String code) {
        super(code, violations, message);
    }
}
