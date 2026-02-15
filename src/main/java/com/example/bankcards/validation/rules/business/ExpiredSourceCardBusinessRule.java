package com.example.bankcards.validation.rules.business;

import com.example.bankcards.entity.TransferData;
import com.example.bankcards.entity.TransferViolation;
import com.example.bankcards.entity.Violation;
import com.example.bankcards.util.CardUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Optional;

@Component
public class ExpiredSourceCardBusinessRule implements BusinessRule<TransferData> {
    @Override
    public Optional<TransferViolation> applyRule(TransferData data) {
        if (CardUtils.isExpired(data.from(), LocalDate.now())) {
            return Optional.of(new TransferViolation(
                    new Violation("fromCardId", "Source card is expired."),
                    Optional.of(data.from().getId())
            ));
        }
        return Optional.empty();
    }
}
