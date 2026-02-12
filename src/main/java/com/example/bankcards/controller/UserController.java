package com.example.bankcards.controller;

import com.example.bankcards.dto.UserRequest;
import com.example.bankcards.dto.UserResponse;
import com.example.bankcards.service.CreateUserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@RestController
@RequestMapping("/api/v1")
public class UserController {
    private final CreateUserService createUserService;

    @PostMapping("/users")
    public UserResponse createUser(@RequestBody UserRequest request) {
        return createUserService.createUser(request);
    }

    @GetMapping("/users")
    public UserResponse getUserList() {
        return new UserResponse(1L, "user1", null);
    }
}
