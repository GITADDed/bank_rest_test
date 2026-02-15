package com.example.bankcards.exception;

import com.example.bankcards.entity.Violation;
import lombok.Getter;

import java.util.List;

@Getter
public class ConflictException extends BaseException {
    public ConflictException(List<Violation> violations, String message, String code) {
        super(code, violations, message);
    }
}
