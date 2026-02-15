package com.example.bankcards.exception;

import com.example.bankcards.entity.Violation;

import java.util.List;

public class ForbiddenException extends BaseException {
    public ForbiddenException(List<Violation> violations) {
        super("FORBIDDEN_ERROR", violations, "You do not have permission to perform this action.");
    }
}
