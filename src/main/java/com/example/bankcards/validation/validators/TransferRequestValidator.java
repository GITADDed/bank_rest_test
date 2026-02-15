package com.example.bankcards.validation.validators;

import com.example.bankcards.dto.TransferRequest;
import com.example.bankcards.validation.rules.ValidationRule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class TransferRequestValidator implements RequestValidator<TransferRequest> {

    private final List<ValidationRule<TransferRequest>> validationRules;

    @Override
    public void validate(TransferRequest request) {
        this.validate(request, validationRules);
    }
}
