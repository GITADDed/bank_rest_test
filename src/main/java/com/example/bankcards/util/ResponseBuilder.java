package com.example.bankcards.util;

import com.example.bankcards.dto.ErrorDetail;
import com.example.bankcards.dto.ErrorResponse;

import java.util.List;

public class ResponseBuilder {
    public static ErrorResponse buildErrorResponse(String code, String message, List<ErrorDetail> details) {
        return new ErrorResponse(code, message, details);
    }
}
