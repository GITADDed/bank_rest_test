package com.example.bankcards.validation.rules.business;

import com.example.bankcards.dto.TransferRequest;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.TransferData;
import com.example.bankcards.entity.TransferViolation;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.Violation;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BlockedSourceCardBusinessRuleTest {

    private final BlockedSourceCardBusinessRule rule = new BlockedSourceCardBusinessRule();

    @Test
    void applyRule_returnsViolation_whenSourceCardBlocked() {
        Card from = buildCard(CardStatus.BLOCKED);
        Card to = buildCard(CardStatus.ACTIVE);
        TransferData data = new TransferData(from, to, new TransferRequest(1L, 2L, new BigDecimal("1.00")));

        Optional<TransferViolation> result = rule.applyRule(data);

        assertTrue(result.isPresent());
        TransferViolation violation = result.get();
        assertEquals(Optional.empty(), violation.expiredCardIdToMark());
        Violation v = violation.violation();
        assertEquals("fromCardId", v.field());
        assertEquals("Source card is blocked.", v.message());
    }

    @Test
    void applyRule_returnsEmpty_whenSourceCardNotBlocked() {
        Card from = buildCard(CardStatus.ACTIVE);
        Card to = buildCard(CardStatus.ACTIVE);
        TransferData data = new TransferData(from, to, new TransferRequest(1L, 2L, new BigDecimal("1.00")));

        Optional<TransferViolation> result = rule.applyRule(data);

        assertTrue(result.isEmpty());
    }

    private Card buildCard(CardStatus status) {
        User owner = new User("user", "hash", Set.of(Role.USER));
        return new Card(owner, "panHash", "1234", 1, 2030, status, BigDecimal.ZERO);
    }
}

