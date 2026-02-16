package com.example.bankcards.util;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CardUtilsTest {

    @Test
    void isExpired_returnsTrue_whenYearInPast() {
        LocalDate now = LocalDate.now();
        Card card = buildCard(now.getMonthValue(), now.getYear() - 1);

        boolean result = CardUtils.isExpired(card, now);

        assertTrue(result);
    }

    @Test
    void isExpired_returnsTrue_whenMonthInPastSameYear() {
        LocalDate now = LocalDate.now();
        int month = now.getMonthValue() - 1;
        int year = now.getYear();
        if (month == 0) {
            month = 12;
            year -= 1;
        }
        Card card = buildCard(month, year);

        boolean result = CardUtils.isExpired(card, now);

        assertTrue(result);
    }

    @Test
    void isExpired_returnsFalse_whenSameMonthAndYear() {
        LocalDate now = LocalDate.now();
        Card card = buildCard(now.getMonthValue(), now.getYear());

        boolean result = CardUtils.isExpired(card, now);

        assertFalse(result);
    }

    @Test
    void isExpired_returnsFalse_whenMonthInFutureSameYear() {
        LocalDate now = LocalDate.now();
        int month = now.getMonthValue() + 1;
        int year = now.getYear();
        if (month == 13) {
            month = 1;
            year += 1;
        }
        Card card = buildCard(month, year);

        boolean result = CardUtils.isExpired(card, now);

        assertFalse(result);
    }

    private Card buildCard(int expiryMonth, int expiryYear) {
        User owner = new User("user", "hash", Set.of(Role.USER));
        Card card = new Card(owner, "panHash", "1234", expiryMonth, expiryYear, CardStatus.ACTIVE, BigDecimal.ZERO);
        return card;
    }
}

