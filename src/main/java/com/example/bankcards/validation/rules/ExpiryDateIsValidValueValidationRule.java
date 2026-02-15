package com.example.bankcards.validation.rules;

import com.example.bankcards.dto.CardRequest;
import com.example.bankcards.entity.Violation;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Order(1)
@Component
public class ExpiryDateIsValidValueValidationRule implements ValidationRule<CardRequest> {
    @Override
    public Optional<Violation> applyRule(CardRequest request) {
        if (request.expiryMonth() < 1 || request.expiryMonth() > 12)
            return Optional.of(new Violation("expiryMonth", "Expiry month must be between 1 and 12."));

        if (request.expiryYear() < 0)
            return Optional.of(new Violation("expiryYear", "Expiry year must be positive."));

        return Optional.empty();
    }

    @Override
    public boolean stopOnFailure() {
        return true;
    }
}
