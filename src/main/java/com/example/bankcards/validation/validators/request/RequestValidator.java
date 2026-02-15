package com.example.bankcards.validation.validators.request;

import com.example.bankcards.entity.Violation;
import com.example.bankcards.exception.ValidationException;
import com.example.bankcards.validation.rules.request.Rule;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface RequestValidator<T> {
    void validate(T request);

    default void validate(T request, List<Rule<T>> rules) {
        List<Violation> errors = new ArrayList<>();

        for (Rule<T> rule : rules) {
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
