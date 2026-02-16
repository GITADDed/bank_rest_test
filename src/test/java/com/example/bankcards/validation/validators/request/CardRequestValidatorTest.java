package com.example.bankcards.validation.validators.request;

import com.example.bankcards.dto.CardRequest;
import com.example.bankcards.entity.Violation;
import com.example.bankcards.exception.ValidationException;
import com.example.bankcards.validation.rules.request.Rule;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CardRequestValidatorTest {

    @Test
    void validate_throwsWithAllViolations_whenMultipleRulesFail() {
        CardRequest request = new CardRequest(1L, "1234567890123456", 12, 2030);
        Rule<CardRequest> rule1 = new TestRule(Optional.of(new Violation("pan", "Invalid.")), false);
        Rule<CardRequest> rule2 = new TestRule(Optional.of(new Violation("expiryYear", "Past.")), false);

        CardRequestValidator validator = new CardRequestValidator(List.of(rule1, rule2));

        ValidationException ex = assertThrows(ValidationException.class, () -> validator.validate(request));

        assertEquals("VALIDATION_ERROR", ex.getCode());
        assertEquals(2, ex.getViolations().size());
        assertEquals("pan", ex.getViolations().get(0).field());
        assertEquals("expiryYear", ex.getViolations().get(1).field());
    }

    @Test
    void validate_stopsOnFailure_whenRuleRequestsStop() {
        CardRequest request = new CardRequest(1L, "1234567890123456", 12, 2030);
        TrackingRule rule1 = new TrackingRule(Optional.of(new Violation("pan", "Invalid.")), true);
        TrackingRule rule2 = new TrackingRule(Optional.of(new Violation("expiryYear", "Past.")), false);

        CardRequestValidator validator = new CardRequestValidator(List.of(rule1, rule2));

        assertThrows(ValidationException.class, () -> validator.validate(request));

        assertEquals(1, rule1.invocations);
        assertFalse(rule2.wasInvoked());
    }

    @Test
    void validate_returnsNormally_whenNoViolations() {
        CardRequest request = new CardRequest(1L, "1234567890123456", 12, 2030);
        Rule<CardRequest> rule = new TestRule(Optional.empty(), false);

        CardRequestValidator validator = new CardRequestValidator(List.of(rule));

        validator.validate(request);
    }

    private static class TestRule implements Rule<CardRequest> {
        private final Optional<Violation> violation;
        private final boolean stopOnFailure;

        private TestRule(Optional<Violation> violation, boolean stopOnFailure) {
            this.violation = violation;
            this.stopOnFailure = stopOnFailure;
        }

        @Override
        public Optional<Violation> applyRule(CardRequest request) {
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
        public Optional<Violation> applyRule(CardRequest request) {
            invocations++;
            return super.applyRule(request);
        }

        private boolean wasInvoked() {
            return invocations > 0;
        }
    }
}
