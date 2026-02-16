package com.example.bankcards.service;

import com.example.bankcards.dto.UserRequest;
import com.example.bankcards.dto.UserResponse;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.Violation;
import com.example.bankcards.exception.NotFoundException;
import com.example.bankcards.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl service;

    @Test
    void createUser_savesAndReturnsDto() {
        UserRequest request = new UserRequest("user", "pass", Set.of(Role.USER));

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            ReflectionTestUtils.setField(user, "id", 10L);
            return user;
        });

        UserResponse response = service.createUser(request);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User saved = captor.getValue();
        assertNotNull(saved);
        assertEquals("user", saved.getUsername());
        assertEquals("pass", saved.getPasswordHash());
        assertEquals(Set.of(Role.USER), saved.getRoles());

        assertEquals(10L, response.id());
        assertEquals("user", response.username());
        assertEquals(Set.of(Role.USER), response.role());
    }

    @Test
    void getAllUsers_mapsToDto() {
        User user = new User("user", "hash", Set.of(Role.ADMIN));
        ReflectionTestUtils.setField(user, "id", 5L);
        PageRequest pageable = PageRequest.of(0, 10);

        Page<User> page = new PageImpl<>(List.of(user), pageable, 1);

        when(userRepository.findAllByDeletedFalse(pageable)).thenReturn(page);

        Page<UserResponse> result = service.getAllUsers(pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(5L, result.getContent().get(0).id());
        assertEquals("user", result.getContent().get(0).username());
        assertEquals(Set.of(Role.ADMIN), result.getContent().get(0).role());
        assertEquals(0, result.getNumber());
        assertEquals(10, result.getSize());
        verify(userRepository).findAllByDeletedFalse(pageable);
    }

    @Test
    void getUserById_returnsDto_whenFound() {
        User user = new User("user", "hash", Set.of(Role.USER));
        ReflectionTestUtils.setField(user, "id", 7L);

        when(userRepository.findByIdAndDeletedFalse(7L)).thenReturn(Optional.of(user));

        UserResponse response = service.getUserById(7L);

        assertEquals(7L, response.id());
        assertEquals("user", response.username());
        verify(userRepository).findByIdAndDeletedFalse(7L);
    }

    @Test
    void getUserById_throwsNotFound_whenMissing() {
        when(userRepository.findByIdAndDeletedFalse(99L)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class, () -> service.getUserById(99L));

        assertEquals("NOT_FOUND_ERROR", ex.getCode());
        Violation violation = ex.getViolations().get(0);
        assertEquals("id", violation.field());
        assertEquals("User with id 99 not found.", violation.message());
    }

    @Test
    void updateUserRole_updatesAndReturnsDto() {
        User user = new User("user", "hash", Set.of(Role.USER));
        ReflectionTestUtils.setField(user, "id", 12L);
        Set<Role> newRoles = Set.of(Role.ADMIN);

        when(userRepository.findById(12L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        UserResponse response = service.updateUserRole(12L, newRoles);

        assertEquals(Set.of(Role.ADMIN), response.role());
        assertEquals("user", response.username());
        assertEquals(Set.of(Role.ADMIN), user.getRoles());
        verify(userRepository).save(user);
    }

    @Test
    void updateUserRole_throwsNotFound_whenMissing() {
        when(userRepository.findById(100L)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class, () -> service.updateUserRole(100L, Set.of(Role.USER)));

        assertEquals("NOT_FOUND_ERROR", ex.getCode());
        Violation violation = ex.getViolations().get(0);
        assertEquals("id", violation.field());
        assertEquals("User with id 100 not found.", violation.message());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void deleteUser_marksDeletedAndSaves() {
        User user = new User("user", "hash", Set.of(Role.USER));
        ReflectionTestUtils.setField(user, "id", 15L);

        when(userRepository.findByIdAndDeletedFalse(15L)).thenReturn(Optional.of(user));

        service.deleteUser(15L);

        assertEquals(true, user.getDeleted());
        assertEquals(Set.of(Role.USER), user.getRoles());
        verify(userRepository).save(user);
    }

    @Test
    void deleteUser_throwsNotFound_whenMissing() {
        when(userRepository.findByIdAndDeletedFalse(16L)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class, () -> service.deleteUser(16L));

        assertEquals("NOT_FOUND_ERROR", ex.getCode());
        Violation violation = ex.getViolations().get(0);
        assertEquals("id", violation.field());
        assertEquals("User with id 16 not found.", violation.message());
        verify(userRepository, never()).save(any(User.class));
    }
}
