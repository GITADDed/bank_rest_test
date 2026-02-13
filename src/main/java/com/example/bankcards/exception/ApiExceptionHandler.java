package com.example.bankcards.exception;

import com.example.bankcards.dto.ErrorDetail;
import com.example.bankcards.dto.ErrorResponse;
import com.example.bankcards.util.ResponseBuilder;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidation(ValidationException ex, HttpServletRequest req) {
        List<ErrorDetail> details = ex.getViolations().stream()
                .map(v -> new ErrorDetail(v.field(), v.message()))
                .toList();

        var body = ResponseBuilder.buildErrorResponse(ex.getCode(), ex.getMessage(), details);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NotFoundException ex, HttpServletRequest req) {
        List<ErrorDetail> details = ex.getViolations().stream()
                .map(v -> new ErrorDetail(v.field(), v.message()))
                .toList();

        var body = ResponseBuilder.buildErrorResponse(ex.getCode(), ex.getMessage(), details);

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<ApiError> handleAny(Exception ex, HttpServletRequest req) {
//        var body = new ApiError(
//                "INTERNAL_ERROR",
//                "Unexpected error",
//                Instant.now(),
//                req.getRequestURI(),
//                null
//        );
//        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
//    }
}
