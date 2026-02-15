package com.example.bankcards.util;

import com.example.bankcards.entity.Card;

import java.time.LocalDate;

public class CardUtils {
    public static boolean isExpired(Card card, LocalDate now) {
        int y = card.getExpiryYear();
        int m = card.getExpiryMonth();
        return (y < now.getYear()) || (y == now.getYear() && m < now.getMonthValue());
    }
}
