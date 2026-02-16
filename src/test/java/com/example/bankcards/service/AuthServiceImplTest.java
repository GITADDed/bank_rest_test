package com.example.bankcards.service;

import com.example.bankcards.config.JwtConfig;
import com.example.bankcards.dto.LoginRequest;
import com.example.bankcards.dto.LoginResponse;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtEncoder;

import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class AuthServiceImplTest {

    @Mock AuthenticationManager authenticationManager;
    @Mock JwtEncoder jwtEncoder;
    @Mock JwtConfig jwtConfig;
    @Mock UserRepository userRepository;

    @InjectMocks AuthServiceImpl authService;

    @Test
    void login_success_returnsToken() {
        // given
        LoginRequest req = new LoginRequest("bob", "pass");
        Authentication auth = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(auth);

        when(auth.getName()).thenReturn("bob");
        when(auth.getAuthorities()).thenAnswer(inv ->
                List.of(
                        new SimpleGrantedAuthority("ROLE_USER"),
                        new SimpleGrantedAuthority("ROLE_ADMIN")
                )
        );
        User user = mock(User.class);
        when(user.getId()).thenReturn(42L);
        when(userRepository.findByUsernameAndDeletedFalse("bob")).thenReturn(Optional.of(user));

        when(jwtConfig.getTtlMinutes()).thenReturn(15L);

        // JwtEncoder.encode(...) -> Jwt
        Jwt jwt = new Jwt(
                "token-value",
                Instant.now(),
                Instant.now().plusSeconds(60),
                Map.of("alg", "HS256"),
                Map.of("sub", "bob")
        );
        when(jwtEncoder.encode(any())).thenReturn(jwt);

        // when
        LoginResponse resp = authService.login(req);

        // then
        assertEquals("token-value", resp.accessToken());

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository).findByUsernameAndDeletedFalse("bob");
        verify(jwtEncoder).encode(any());
    }

    @Test
    void login_whenUserNotFound_throwsUsernameNotFoundException() {
        // given
        LoginRequest req = new LoginRequest("bob", "pass");
        Authentication auth = mock(Authentication.class);
        when(authenticationManager.authenticate(any())).thenReturn(auth);
        when(auth.getName()).thenReturn("bob");

        when(userRepository.findByUsernameAndDeletedFalse("bob")).thenReturn(Optional.empty());

        // when / then
        assertThrows(UsernameNotFoundException.class, () -> authService.login(req));
        verify(jwtEncoder, never()).encode(any());
    }
}
