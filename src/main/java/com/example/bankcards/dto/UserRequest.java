package com.example.bankcards.dto;

import com.example.bankcards.entity.Role;
import jakarta.validation.constraints.NotBlank;

import java.util.Set;

public record UserRequest(
        @NotBlank(message = "Username must not be blank.")
        String username,

        @NotBlank(message = "Password must not be blank.")
        String password,

        Set<Role> role
) {
    public UserRequest {
        if (role == null)
            role = Set.of(Role.USER);
    }
}
