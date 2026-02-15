package com.example.bankcards.exception;

import com.example.bankcards.entity.Violation;
import lombok.Getter;

import java.util.List;

@Getter
public class NotFoundException extends BaseException {
    public NotFoundException(List<Violation> violations) {
        super("NOT_FOUND_ERROR", violations, "Not found some resource.");
    }
}
