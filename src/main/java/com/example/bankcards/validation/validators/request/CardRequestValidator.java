package com.example.bankcards.validation.validators.request;

import com.example.bankcards.dto.CardRequest;
import com.example.bankcards.validation.rules.request.Rule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class CardRequestValidator implements RequestValidator<CardRequest> {

    private final List<Rule<CardRequest>> rules;

    @Override
    public void validate(CardRequest request) {
        this.validate(request, rules);
    }
}
