package com.example.bankcards.exception;

import com.example.bankcards.dto.ErrorDetail;
import com.example.bankcards.dto.ErrorResponse;
import com.example.bankcards.util.ResponseBuilder;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidation(ValidationException ex) {
        var body = fromExceptionToErrorResponse(ex);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NotFoundException ex) {
        var body = fromExceptionToErrorResponse(ex);

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> handleConflictException(ConflictException ex) {
        var body = fromExceptionToErrorResponse(ex);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {

        List<ErrorDetail> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> new ErrorDetail(
                        error.getField(),
                        error.getDefaultMessage()
                ))
                .toList();
        var body = ResponseBuilder.buildErrorResponse(
                "VALIDATION_ERROR",
                "Validation failed for one or more fields.",
                errors
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponse> handleForbiddenException(ForbiddenException ex) {
        var body = fromExceptionToErrorResponse(ex);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedException(UnauthorizedException ex) {
        var body = fromExceptionToErrorResponse(ex);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

    private ErrorResponse fromExceptionToErrorResponse(BaseException ex) {
        List<ErrorDetail> details = ex.getViolations().stream()
                .map(v -> new ErrorDetail(v.field(), v.message()))
                .toList();

        return ResponseBuilder.buildErrorResponse(ex.getCode(), ex.getMessage(), details);
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
