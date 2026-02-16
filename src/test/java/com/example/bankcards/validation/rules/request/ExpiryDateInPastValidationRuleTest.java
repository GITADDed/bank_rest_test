package com.example.bankcards.validation.rules.request;

import com.example.bankcards.dto.CardRequest;
import com.example.bankcards.entity.Violation;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExpiryDateInPastValidationRuleTest {

    private final ExpiryDateInPastValidationRule rule = new ExpiryDateInPastValidationRule();

    @Test
    void applyRule_returnsViolation_whenYearInPast() {
        LocalDate now = LocalDate.now();
        int pastYear = now.getYear() - 1;
        CardRequest request = new CardRequest(1L, "1234567890123456", 12, pastYear);

        Optional<Violation> result = rule.applyRule(request);

        assertTrue(result.isPresent());
        Violation violation = result.get();
        assertEquals("expiryYear", violation.field());
        assertEquals("Expiry year must be the current year or a future year.", violation.message());
    }

    @Test
    void applyRule_returnsViolation_whenMonthInPastSameYear() {
        LocalDate now = LocalDate.now();
        int year = now.getYear();
        int month = now.getMonthValue() - 1;
        if (month == 0) {
            month = 1;
        }
        CardRequest request = new CardRequest(1L, "1234567890123456", month, year);

        Optional<Violation> result = rule.applyRule(request);

        assertTrue(result.isPresent());
        Violation violation = result.get();
        assertEquals("expiryMonth", violation.field());
        assertEquals("Expiry month must be the current month or a future month.", violation.message());
    }

    @Test
    void applyRule_returnsEmpty_whenCurrentMonthOrFuture() {
        LocalDate now = LocalDate.now();
        int year = now.getYear();
        int month = now.getMonthValue();
        CardRequest request = new CardRequest(1L, "1234567890123456", month, year);

        Optional<Violation> result = rule.applyRule(request);

        assertTrue(result.isEmpty());
    }
}

