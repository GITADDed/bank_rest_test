package com.example.bankcards.validation.rules;

import com.example.bankcards.dto.CardRequest;
import com.example.bankcards.entity.Violation;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Order(0)
@Component
public class PANNullEmptyValidationRule implements ValidationRule<CardRequest> {
    @Override
    public Optional<Violation> applyRule(CardRequest request) {
        String pan = request.pan();

        if (pan == null || pan.isEmpty())
            return Optional.of(new Violation("pan", "PAN must be not null or empty."));

        return Optional.empty();
    }

    @Override
    public boolean stopOnFailure() {
        return true;
    }
}
