package com.example.bankcards.dto;

import com.example.bankcards.entity.Role;

import java.util.Set;

public record UpdateRolesRequest(
        Set<Role> roles
) {
}