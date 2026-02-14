package com.example.bankcards.validation.rules;

import com.example.bankcards.dto.CardRequest;
import com.example.bankcards.util.Violation;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Optional;

@Order(2)
@Component
public class ExpiryDateInPastValidationRule implements ValidationRule<CardRequest> {
    @Override
    public Optional<Violation> applyRule(CardRequest request) {
        LocalDate currentDate = LocalDate.now();

        if (currentDate.getYear() > request.expiryYear())
            return Optional.of(new Violation("expiryYear", "Expiry year must be the current year or a future year."));

        if (currentDate.getYear() == request.expiryYear() && currentDate.getMonthValue() > request.expiryMonth())
            return Optional.of(new Violation("expiryMonth", "Expiry month must be the current month or a future month."));

        return Optional.empty();
    }
}
