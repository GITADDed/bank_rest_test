package com.example.bankcards.service;

import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DbUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private DbUserDetailsService service;

    @Test
    void loadUserByUsername_returnsUserDetails_whenFound() {
        String username = "user1";
        String passwordHash = "hash";
        User user = new User(username, passwordHash, Set.of(Role.USER, Role.ADMIN));

        when(userRepository.findByUsernameAndDeletedFalse(username)).thenReturn(Optional.of(user));

        UserDetails details = service.loadUserByUsername(username);

        assertEquals(username, details.getUsername());
        assertEquals(passwordHash, details.getPassword());
        assertEquals(2, details.getAuthorities().size());
        assertTrue(details.getAuthorities().stream()
                .map(a -> a.getAuthority())
                .collect(Collectors.toSet())
                .containsAll(Set.of("ROLE_USER", "ROLE_ADMIN")));
        verify(userRepository).findByUsernameAndDeletedFalse(username);
    }

    @Test
    void loadUserByUsername_throws_whenMissing() {
        String username = "missing";

        when(userRepository.findByUsernameAndDeletedFalse(username)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> service.loadUserByUsername(username));
        verify(userRepository).findByUsernameAndDeletedFalse(username);
    }
}

