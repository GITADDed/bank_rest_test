package com.example.bankcards.service;

import com.example.bankcards.dto.LoginRequest;
import com.example.bankcards.dto.LoginResponse;

public interface AuthService {
    LoginResponse login(LoginRequest req);
}
