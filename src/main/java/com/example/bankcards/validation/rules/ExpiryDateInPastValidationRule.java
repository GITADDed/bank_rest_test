package com.example.bankcards.validation.rules;

import com.example.bankcards.dto.CardRequest;
import com.example.bankcards.util.Violation;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Optional;

@Component
public class ExpiryDateInPastValidationRule implements ValidationRule<CardRequest> {
    @Override
    public Optional<Violation> applyRule(CardRequest request) {
        LocalDate currentDate = LocalDate.now();

        if (currentDate.getYear() > request.expiryYear())
            return Optional.of(new Violation("expiryYear", "Expiry year must be the current year or a future year."));

        return Optional.empty();
    }
}
