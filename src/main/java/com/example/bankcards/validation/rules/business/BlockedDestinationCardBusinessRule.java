package com.example.bankcards.validation.rules.business;

import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.TransferData;
import com.example.bankcards.entity.TransferViolation;
import com.example.bankcards.entity.Violation;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class BlockedDestinationCardBusinessRule implements BusinessRule<TransferData> {
    @Override
    public Optional<TransferViolation> applyRule(TransferData data) {
        if (data.to().getStatus() == CardStatus.BLOCKED) {
            return Optional.of(new TransferViolation(new Violation("toCardId",
                    "Destination card is blocked."), Optional.empty()));
        }
        return Optional.empty();
    }
}
