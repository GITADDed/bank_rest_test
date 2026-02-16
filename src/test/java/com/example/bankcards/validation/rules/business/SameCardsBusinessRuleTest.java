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
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SameCardsBusinessRuleTest {

    private final SameCardsBusinessRule rule = new SameCardsBusinessRule();

    @Test
    void applyRule_returnsViolation_whenSameCard() {
        Card from = buildCard(1L);
        Card to = buildCard(1L);
        TransferData data = new TransferData(from, to, new TransferRequest(1L, 1L, new BigDecimal("1.00")));

        Optional<TransferViolation> result = rule.applyRule(data);

        assertTrue(result.isPresent());
        TransferViolation violation = result.get();
        assertEquals(Optional.empty(), violation.expiredCardIdToMark());
        Violation v = violation.violation();
        assertEquals("toCardId", v.field());
        assertEquals("Cannot transfer to the same card.", v.message());
    }

    @Test
    void applyRule_returnsEmpty_whenDifferentCards() {
        Card from = buildCard(1L);
        Card to = buildCard(2L);
        TransferData data = new TransferData(from, to, new TransferRequest(1L, 2L, new BigDecimal("1.00")));

        Optional<TransferViolation> result = rule.applyRule(data);

        assertTrue(result.isEmpty());
    }

    private Card buildCard(Long id) {
        User owner = new User("user", "hash", Set.of(Role.USER));
        Card card = new Card(owner, "panHash", "1234", 1, 2030, CardStatus.ACTIVE, BigDecimal.ZERO);
        ReflectionTestUtils.setField(card, "id", id);
        return card;
    }
}

