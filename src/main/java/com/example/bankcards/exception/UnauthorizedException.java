package com.example.bankcards.exception;

import java.util.List;

public class UnauthorizedException extends BaseException {
    public UnauthorizedException() {
        super("UNAUTHORIZED_ERROR", List.of(), "Unauthorized access.");
    }
}
