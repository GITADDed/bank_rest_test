package com.example.bankcards.validation.validators;

import com.example.bankcards.dto.CardRequest;
import com.example.bankcards.exception.ValidationException;
import com.example.bankcards.util.Violation;
import com.example.bankcards.validation.rules.ValidationRule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class CardRequestValidator implements RequestValidator<CardRequest> {

    private final List<ValidationRule<CardRequest>> validationRules;

//    @Override
//    public void validate(CardRequest request) {
//        List<Violation> errors = validationRules.stream()
//                .map(rule -> rule.applyRule(request))
//                .filter(Optional::isPresent)
//                .map(Optional::get)
//                .toList();
//        if (!errors.isEmpty()) {
//            throw new ValidationException(errors);
//        }
//    }

    @Override
    public void validate(CardRequest request) {
        List<Violation> errors = new ArrayList<>();

        for (ValidationRule<CardRequest> rule : validationRules) {
            Optional<Violation> v = rule.applyRule(request);
            if (v.isPresent()) {
                errors.add(v.get());
                if (rule.stopOnFailure()) {
                    break;
                }
            }
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }
}
