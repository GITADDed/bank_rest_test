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

class CheckBalanceBusinessRuleTest {

    private final CheckBalanceBusinessRule rule = new CheckBalanceBusinessRule();

    @Test
    void applyRule_returnsViolation_whenInsufficientBalance() {
        Card from = buildCard(new BigDecimal("5.00"));
        Card to = buildCard(BigDecimal.ZERO);
        TransferRequest request = new TransferRequest(10L, 11L, new BigDecimal("10.00"));
        TransferData data = new TransferData(from, to, request);

        Optional<TransferViolation> result = rule.applyRule(data);

        assertTrue(result.isPresent());
        TransferViolation violation = result.get();
        Violation v = violation.violation();
        assertEquals("amount", v.field());
        assertEquals("Not enough money on card with id " + request.fromCardId() + ".", v.message());
        assertEquals(Optional.empty(), violation.expiredCardIdToMark());
    }

    @Test
    void applyRule_returnsEmpty_whenBalanceSufficient() {
        Card from = buildCard(new BigDecimal("10.00"));
        Card to = buildCard(BigDecimal.ZERO);
        TransferRequest request = new TransferRequest(10L, 11L, new BigDecimal("10.00"));
        TransferData data = new TransferData(from, to, request);

        Optional<TransferViolation> result = rule.applyRule(data);

        assertTrue(result.isEmpty());
    }

    private Card buildCard(BigDecimal balance) {
        User owner = new User("user", "hash", Set.of(Role.USER));
        return new Card(owner, "panHash", "1234", 1, 2030, CardStatus.ACTIVE, balance);
    }
}

