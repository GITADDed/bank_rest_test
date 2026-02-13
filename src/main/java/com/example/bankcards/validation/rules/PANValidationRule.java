package com.example.bankcards.validation.rules;

import com.example.bankcards.dto.CardRequest;
import com.example.bankcards.util.Violation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class PANValidationRule implements ValidationRule<CardRequest> {

    @Override
    public Optional<Violation> applyRule(CardRequest request) {
        String pan = request.pan();

        // Because check for null and empty in another rule.
        if (pan == null || pan.isEmpty())
            return Optional.empty();

        boolean isOnlyDigits = StringUtils.isNumeric(pan);
        int length = pan.length();

        if (!isOnlyDigits || length != 16) {
            return Optional.of(new Violation("pan", "PAN must be a 16-digit number."));
        }

        return Optional.empty();
    }
}
