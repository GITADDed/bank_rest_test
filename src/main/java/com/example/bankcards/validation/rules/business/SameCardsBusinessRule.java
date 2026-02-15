package com.example.bankcards.validation.rules.business;

import com.example.bankcards.entity.TransferData;
import com.example.bankcards.entity.TransferViolation;
import com.example.bankcards.entity.Violation;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SameCardsBusinessRule implements BusinessRule<TransferData> {
    @Override
    public Optional<TransferViolation> applyRule(TransferData data) {
        if (data.from().getId().equals(data.to().getId())) {
            return Optional.of(new TransferViolation(new Violation("toCardId",
                    "Cannot transfer to the same card."), Optional.empty()));
        }
        return Optional.empty();
    }
}
