package com.example.bankcards.util;

import com.example.bankcards.dto.ErrorDetail;
import com.example.bankcards.dto.ErrorResponse;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class ResponseBuilderTest {

    @Test
    void buildErrorResponse_returnsExpectedRecord() {
        List<ErrorDetail> details = List.of(new ErrorDetail("field", "message"));

        ErrorResponse response = ResponseBuilder.buildErrorResponse("CODE", "Error message", details);

        assertEquals("CODE", response.code());
        assertEquals("Error message", response.message());
        assertSame(details, response.details());
    }
}

