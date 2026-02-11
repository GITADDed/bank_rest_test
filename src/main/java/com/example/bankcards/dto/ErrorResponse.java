package com.example.bankcards.dto;

public record ErrorResponse(String code,
                            String message,
                            List<ErrorDetail> details,
                            String traceId) {
}
