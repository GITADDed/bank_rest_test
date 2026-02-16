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
import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExpiredDestinationCardBusinessRuleTest {

    private final ExpiredDestinationCardBusinessRule rule = new ExpiredDestinationCardBusinessRule();

    @Test
    void applyRule_returnsViolation_whenDestinationExpired() {
        LocalDate now = LocalDate.now();
        int month = now.getMonthValue() - 1;
        int year = now.getYear();
        if (month == 0) {
            month = 12;
            year -= 1;
        }

        Card from = buildCard(1L, now.getMonthValue(), now.getYear());
        Card to = buildCard(2L, month, year);
        TransferData data = new TransferData(from, to, new TransferRequest(1L, 2L, new BigDecimal("1.00")));

        Optional<TransferViolation> result = rule.applyRule(data);

        assertTrue(result.isPresent());
        TransferViolation violation = result.get();
        assertEquals(Optional.of(2L), violation.expiredCardIdToMark());
        Violation v = violation.violation();
        assertEquals("toCardId", v.field());
        assertEquals("Destination card is expired", v.message());
    }

    @Test
    void applyRule_returnsEmpty_whenDestinationNotExpired() {
        LocalDate now = LocalDate.now();
        int month = now.getMonthValue() + 1;
        int year = now.getYear();
        if (month == 13) {
            month = 1;
            year += 1;
        }

        Card from = buildCard(1L, month, year);
        Card to = buildCard(2L, month, year);
        TransferData data = new TransferData(from, to, new TransferRequest(1L, 2L, new BigDecimal("1.00")));

        Optional<TransferViolation> result = rule.applyRule(data);

        assertTrue(result.isEmpty());
    }

    private Card buildCard(Long id, int expiryMonth, int expiryYear) {
        User owner = new User("user", "hash", Set.of(Role.USER));
        Card card = new Card(owner, "panHash", "1234", expiryMonth, expiryYear, CardStatus.ACTIVE, BigDecimal.ZERO);
        ReflectionTestUtils.setField(card, "id", id);
        return card;
    }
}

