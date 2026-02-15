package com.example.bankcards.service;

import com.example.bankcards.dto.UserRequest;
import com.example.bankcards.dto.UserResponse;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.Violation;
import com.example.bankcards.exception.NotFoundException;
import com.example.bankcards.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserResponse createUser(UserRequest request) {
        User user = new User(request.username(), request.password(), request.role());
        user = userRepository.save(user);
        return user.toDTO();
    }

    @Override
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        return userRepository.findAllByDeletedFalse(pageable).map(User::toDTO);
    }

    @Override
    public UserResponse getUserById(Long id) {
        return userRepository.findByIdAndDeletedFalse(id).orElseThrow(() -> new NotFoundException(
                        List.of(
                                new Violation("id",
                                        "User with id " + id + " not found."))))
                .toDTO();
    }

    @Override
    public UserResponse updateUserRole(Long id, Set<Role> role) {
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException(
                        List.of(
                                new Violation("id",
                                        "User with id " + id + " not found."))));

        user.setRoles(role);

        return userRepository.save(user).toDTO();
    }

    @Override
    public void deleteUser(Long id) {
        User user = userRepository.findByIdAndDeletedFalse(id).orElseThrow(() -> new NotFoundException(
                        List.of(
                                new Violation("id",
                                        "User with id " + id + " not found."))));

        user.setDeleted(true);
        userRepository.save(user);
    }
}
