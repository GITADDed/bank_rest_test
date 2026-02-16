package com.example.bankcards.validation.rules.request;

import com.example.bankcards.dto.CardRequest;
import com.example.bankcards.entity.Violation;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PANValidationRuleTest {

    private final PANValidationRule rule = new PANValidationRule();

    @Test
    void applyRule_returnsViolation_whenPanNotNumeric() {
        CardRequest request = new CardRequest(1L, "1234abcd90123456", 12, 2030);

        Optional<Violation> result = rule.applyRule(request);

        assertTrue(result.isPresent());
        Violation violation = result.get();
        assertEquals("pan", violation.field());
        assertEquals("PAN must be a 16-digit number.", violation.message());
    }

    @Test
    void applyRule_returnsViolation_whenPanWrongLength() {
        CardRequest request = new CardRequest(1L, "123456789012345", 12, 2030);

        Optional<Violation> result = rule.applyRule(request);

        assertTrue(result.isPresent());
        Violation violation = result.get();
        assertEquals("pan", violation.field());
        assertEquals("PAN must be a 16-digit number.", violation.message());
    }

    @Test
    void applyRule_returnsEmpty_whenPanValid() {
        CardRequest request = new CardRequest(1L, "1234567890123456", 12, 2030);

        Optional<Violation> result = rule.applyRule(request);

        assertTrue(result.isEmpty());
    }
}

