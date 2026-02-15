package com.example.bankcards.controller;

import com.example.bankcards.dto.PageResponse;
import com.example.bankcards.dto.UpdateRolesRequest;
import com.example.bankcards.dto.UserRequest;
import com.example.bankcards.dto.UserResponse;
import com.example.bankcards.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@RestController
@RequestMapping("/admin/users")
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserResponse createUser(@RequestBody UserRequest request) {
        return userService.createUser(request);
    }

    @GetMapping
    public PageResponse<UserResponse> getAllUsers(
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC
            ) Pageable pageable) {
        Page<UserResponse> page = userService.getAllUsers(pageable);
        return new PageResponse<>(page.getContent(), page.getNumber(), page.getSize(),
                page.getTotalElements(), page.getTotalPages());
    }

    @GetMapping("/{id}")
    public UserResponse getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @PatchMapping("/{id}/roles")
    public UserResponse updateUserRole(@PathVariable Long id,
                                       @RequestBody UpdateRolesRequest request) {
        return userService.updateUserRole(id, request.roles());
    }

     @DeleteMapping("/{id}")
     @ResponseStatus(code = HttpStatus.NO_CONTENT)
     public void deleteUser(@PathVariable Long id) {
         userService.deleteUser(id);
     }
}
