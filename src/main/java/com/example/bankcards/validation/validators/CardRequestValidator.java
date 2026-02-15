package com.example.bankcards.validation.validators;

import com.example.bankcards.dto.CardRequest;
import com.example.bankcards.exception.ValidationException;
import com.example.bankcards.entity.Violation;
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

    @Override
    public void validate(CardRequest request) {
        this.validate(request, validationRules);
    }
}
