package com.example.bankcards.dto;

import com.example.bankcards.entity.Role;

import java.util.Set;

public record UserRequest(String username, String password, Set<Role> role) {
    public UserRequest {
        if (role == null)
            role = Set.of(Role.USER);
    }
}
