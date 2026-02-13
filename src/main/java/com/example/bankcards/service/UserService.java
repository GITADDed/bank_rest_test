package com.example.bankcards.service;

import com.example.bankcards.dto.UserRequest;
import com.example.bankcards.dto.UserResponse;

public interface UserService {
    UserResponse createUser(UserRequest request);
}
