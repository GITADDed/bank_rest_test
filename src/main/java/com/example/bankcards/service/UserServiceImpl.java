package com.example.bankcards.service;

import com.example.bankcards.dto.UserRequest;
import com.example.bankcards.dto.UserResponse;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserResponse createUser(UserRequest request) {
        User user = new User(request.username(), request.password(), request.role());
        user = userRepository.save(user);
        return new UserResponse(user.getId(), user.getUsername(), user.getRoles());
    }
}
