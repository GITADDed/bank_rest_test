package com.example.bankcards.dto;

import com.example.bankcards.entity.Role;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public record UpdateRolesRequest(
        @NotNull(message = "Roles cannot be null.")
        Set<Role> roles
) {
}