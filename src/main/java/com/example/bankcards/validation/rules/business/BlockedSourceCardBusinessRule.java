package com.example.bankcards.validation.rules.business;

import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.TransferData;
import com.example.bankcards.entity.TransferViolation;
import com.example.bankcards.entity.Violation;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class BlockedSourceCardBusinessRule implements BusinessRule<TransferData> {
    @Override
    public Optional<TransferViolation> applyRule(TransferData data) {
            if (data.from().getStatus() == CardStatus.BLOCKED) {
                return Optional.of(new TransferViolation(new Violation("fromCardId",
                        "Source card is blocked."), Optional.empty()));
            }
        return Optional.empty();
    }
}
