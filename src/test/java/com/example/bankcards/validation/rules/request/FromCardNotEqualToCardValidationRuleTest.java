package com.example.bankcards.validation.rules.request;

import com.example.bankcards.dto.TransferRequest;
import com.example.bankcards.entity.Violation;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FromCardNotEqualToCardValidationRuleTest {

    private final FromCardNotEqualToCardValidationRule rule = new FromCardNotEqualToCardValidationRule();

    @Test
    void applyRule_returnsViolation_whenSameCard() {
        TransferRequest request = new TransferRequest(1L, 1L, new BigDecimal("1.00"));

        Optional<Violation> result = rule.applyRule(request);

        assertTrue(result.isPresent());
        Violation violation = result.get();
        assertEquals("toCardId", violation.field());
        assertEquals("Cannot transfer to the same card.", violation.message());
        assertTrue(rule.stopOnFailure());
    }

    @Test
    void applyRule_returnsEmpty_whenDifferentCards() {
        TransferRequest request = new TransferRequest(1L, 2L, new BigDecimal("1.00"));

        Optional<Violation> result = rule.applyRule(request);

        assertTrue(result.isEmpty());
    }
}

