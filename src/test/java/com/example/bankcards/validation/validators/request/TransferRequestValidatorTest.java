package com.example.bankcards.validation.validators.request;

import com.example.bankcards.dto.TransferRequest;
import com.example.bankcards.entity.Violation;
import com.example.bankcards.exception.ValidationException;
import com.example.bankcards.validation.rules.request.Rule;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TransferRequestValidatorTest {

    @Test
    void validate_throwsWithAllViolations_whenMultipleRulesFail() {
        TransferRequest request = new TransferRequest(1L, 2L, new BigDecimal("1.00"));
        Rule<TransferRequest> rule1 = new TestRule(Optional.of(new Violation("fromCardId", "Missing.")), false);
        Rule<TransferRequest> rule2 = new TestRule(Optional.of(new Violation("toCardId", "Missing.")), false);

        TransferRequestValidator validator = new TransferRequestValidator(List.of(rule1, rule2));

        ValidationException ex = assertThrows(ValidationException.class, () -> validator.validate(request));

        assertEquals("VALIDATION_ERROR", ex.getCode());
        assertEquals(2, ex.getViolations().size());
        assertEquals("fromCardId", ex.getViolations().get(0).field());
        assertEquals("toCardId", ex.getViolations().get(1).field());
    }

    @Test
    void validate_stopsOnFailure_whenRuleRequestsStop() {
        TransferRequest request = new TransferRequest(1L, 2L, new BigDecimal("1.00"));
        TrackingRule rule1 = new TrackingRule(Optional.of(new Violation("fromCardId", "Missing.")), true);
        TrackingRule rule2 = new TrackingRule(Optional.of(new Violation("toCardId", "Missing.")), false);

        TransferRequestValidator validator = new TransferRequestValidator(List.of(rule1, rule2));

        assertThrows(ValidationException.class, () -> validator.validate(request));

        assertEquals(1, rule1.invocations);
        assertFalse(rule2.wasInvoked());
    }

    @Test
    void validate_returnsNormally_whenNoViolations() {
        TransferRequest request = new TransferRequest(1L, 2L, new BigDecimal("1.00"));
        Rule<TransferRequest> rule = new TestRule(Optional.empty(), false);

        TransferRequestValidator validator = new TransferRequestValidator(List.of(rule));

        validator.validate(request);
    }

    private static class TestRule implements Rule<TransferRequest> {
        private final Optional<Violation> violation;
        private final boolean stopOnFailure;

        private TestRule(Optional<Violation> violation, boolean stopOnFailure) {
            this.violation = violation;
            this.stopOnFailure = stopOnFailure;
        }

        @Override
        public Optional<Violation> applyRule(TransferRequest request) {
            return violation;
        }

        @Override
        public boolean stopOnFailure() {
            return stopOnFailure;
        }
    }

    private static final class TrackingRule extends TestRule {
        private int invocations;

        private TrackingRule(Optional<Violation> violation, boolean stopOnFailure) {
            super(violation, stopOnFailure);
        }

        @Override
        public Optional<Violation> applyRule(TransferRequest request) {
            invocations++;
            return super.applyRule(request);
        }

        private boolean wasInvoked() {
            return invocations > 0;
        }
    }
}
