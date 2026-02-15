package com.example.bankcards.validation.validators.business;

import com.example.bankcards.entity.*;
import com.example.bankcards.validation.rules.business.BusinessRule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class TransferValidatorImpl implements TransferValidator {

    private final List<BusinessRule<TransferData>> businessRules;
    @Override
    public TransferCheckResult validate(TransferData data) {
        List<Violation> errors = new ArrayList<>();
        List<Long> expiredCards = new ArrayList<>();

        for (BusinessRule<TransferData> rule : businessRules) {
            Optional<TransferViolation> v = rule.applyRule(data);
            if (v.isPresent()) {
                errors.add(v.get().violation());
                if (v.get().expiredCardIdToMark().isPresent())
                    expiredCards.add(v.get().expiredCardIdToMark().get());
                if (rule.stopOnFailure()) {
                    break;
                }
            }
        }

        return new TransferCheckResult(errors, expiredCards);
    }
}
