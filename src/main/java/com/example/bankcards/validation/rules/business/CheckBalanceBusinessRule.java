package com.example.bankcards.validation.rules.business;

import com.example.bankcards.entity.TransferData;
import com.example.bankcards.entity.TransferViolation;
import com.example.bankcards.entity.Violation;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CheckBalanceBusinessRule implements BusinessRule<TransferData> {
    @Override
    public Optional<TransferViolation> applyRule(TransferData data) {
        if (data.from().getBalance().compareTo(data.request().amount()) < 0) {
            return Optional.of(new TransferViolation(new Violation("amount",
                    "Not enough money on card with id "
                            + data.request().fromCardId()
                            + "."), Optional.empty()));
        }
        return Optional.empty();
    }
}
