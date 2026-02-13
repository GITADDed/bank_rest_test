package com.example.bankcards.controller;

import com.example.bankcards.dto.UserRequest;
import com.example.bankcards.dto.UserResponse;
import com.example.bankcards.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@RestController
@RequestMapping("/api/v1")
public class UserController {
    private final UserService userService;

    @PostMapping("/users")
    public UserResponse createUser(@RequestBody UserRequest request) {
        return userService.createUser(request);
    }

    @GetMapping("/users")
    public UserResponse getUserList() {
        return new UserResponse(1L, "user1", null);
    }
}
