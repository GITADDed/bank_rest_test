package com.example.bankcards.exception;

import com.example.bankcards.entity.Violation;

import java.util.List;

public class NotFoundException extends BaseException {
    public NotFoundException(List<Violation> violations) {
        super("NOT_FOUND_ERROR", violations, "Not found some resource.");
    }
}
