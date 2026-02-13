package com.example.bankcards.validation.validators;

public interface RequestValidator<T> {
    void validate(T request);
}
