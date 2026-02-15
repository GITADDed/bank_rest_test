package com.example.bankcards.validation.rules;

import com.example.bankcards.dto.TransferRequest;
import com.example.bankcards.entity.Violation;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Order(1)
@Component
public class FromCardNotEqualToCardValidationRule implements ValidationRule<TransferRequest> {
    @Override
    public Optional<Violation> applyRule(TransferRequest request) {
            if (request.fromCardId().equals(request.toCardId())) {
                return Optional.of(new Violation("toCardId", "Cannot transfer to the same card."));
            }
        return Optional.empty();
    }

    @Override
    public boolean stopOnFailure() {
        return true;
    }
}
