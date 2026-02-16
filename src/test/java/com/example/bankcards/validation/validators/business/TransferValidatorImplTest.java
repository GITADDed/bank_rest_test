package com.example.bankcards.validation.validators.business;

import com.example.bankcards.dto.TransferRequest;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.TransferCheckResult;
import com.example.bankcards.entity.TransferData;
import com.example.bankcards.entity.TransferViolation;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.Violation;
import com.example.bankcards.validation.rules.business.BusinessRule;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TransferValidatorImplTest {

    @Test
    void validate_aggregatesViolationsAndExpiredIds() {
        TransferData data = buildData(1L, 2L);
        BusinessRule<TransferData> rule1 = new TestRule(
                Optional.of(new TransferViolation(new Violation("fromCardId", "Expired."), Optional.of(1L))),
                false
        );
        BusinessRule<TransferData> rule2 = new TestRule(
                Optional.of(new TransferViolation(new Violation("amount", "Insufficient."), Optional.empty())),
                false
        );

        TransferValidatorImpl validator = new TransferValidatorImpl(List.of(rule1, rule2));

        TransferCheckResult result = validator.validate(data);

        assertEquals(2, result.violations().size());
        assertEquals(List.of(1L), result.expiredCardIdsToMark());
    }

    @Test
    void validate_stopsOnFailure_whenRuleRequestsStop() {
        TransferData data = buildData(1L, 2L);
        BusinessRule<TransferData> rule1 = new TestRule(
                Optional.of(new TransferViolation(new Violation("toCardId", "Blocked."), Optional.empty())),
                true
        );
        BusinessRule<TransferData> rule2 = new TestRule(
                Optional.of(new TransferViolation(new Violation("amount", "Insufficient."), Optional.empty())),
                false
        );

        TransferValidatorImpl validator = new TransferValidatorImpl(List.of(rule1, rule2));

        TransferCheckResult result = validator.validate(data);

        assertEquals(1, result.violations().size());
        assertEquals("toCardId", result.violations().get(0).field());
    }

    @Test
    void validate_returnsEmpty_whenNoViolations() {
        TransferData data = buildData(1L, 2L);
        BusinessRule<TransferData> rule = new TestRule(Optional.empty(), false);

        TransferValidatorImpl validator = new TransferValidatorImpl(List.of(rule));

        TransferCheckResult result = validator.validate(data);

        assertTrue(result.violations().isEmpty());
        assertTrue(result.expiredCardIdsToMark().isEmpty());
    }

    private TransferData buildData(Long fromId, Long toId) {
        User owner = new User("user", "hash", Set.of(Role.USER));
        Card from = new Card(owner, "panHash", "1111", 1, 2030, CardStatus.ACTIVE, BigDecimal.TEN);
        Card to = new Card(owner, "panHash", "2222", 1, 2030, CardStatus.ACTIVE, BigDecimal.ZERO);
        ReflectionTestUtils.setField(from, "id", fromId);
        ReflectionTestUtils.setField(to, "id", toId);
        return new TransferData(from, to, new TransferRequest(fromId, toId, new BigDecimal("1.00")));
    }

    private static final class TestRule implements BusinessRule<TransferData> {
        private final Optional<TransferViolation> violation;
        private final boolean stopOnFailure;

        private TestRule(Optional<TransferViolation> violation, boolean stopOnFailure) {
            this.violation = violation;
            this.stopOnFailure = stopOnFailure;
        }

        @Override
        public Optional<TransferViolation> applyRule(TransferData data) {
            return violation;
        }

        @Override
        public boolean stopOnFailure() {
            return stopOnFailure;
        }
    }
}

