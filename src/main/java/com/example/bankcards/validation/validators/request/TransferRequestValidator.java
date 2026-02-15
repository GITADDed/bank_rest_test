package com.example.bankcards.validation.validators.request;

import com.example.bankcards.dto.TransferRequest;
import com.example.bankcards.validation.rules.request.Rule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class TransferRequestValidator implements RequestValidator<TransferRequest> {

    private final List<Rule<TransferRequest>> rules;

    @Override
    public void validate(TransferRequest request) {
        this.validate(request, rules);
    }
}
