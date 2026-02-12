package com.example.bankcards.service;

import com.example.bankcards.dto.UserRequest;
import com.example.bankcards.dto.UserResponse;

public interface CreateUserService {
    UserResponse createUser(UserRequest request);
}
