package com.inmobot.backend.security.service;

import com.inmobot.common.exception.DomainException;
import com.inmobot.roles.infrastructure.entity.RoleEntity;
import com.inmobot.security.infrastructure.entity.TokenEntity;
import com.inmobot.security.infrastructure.repository.TokenRepository;
import com.inmobot.security.jwt.JwtTokenProvider;
import com.inmobot.security.model.JwtAuthResponse;
import com.inmobot.security.model.LoginRequest;
import com.inmobot.security.service.AuthService;
import com.inmobot.users.infrastructure.entity.UserEntity;
import com.inmobot.users.infrastructure.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenRepository tokenRepository;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(authService, "jwtExpirationMs", 3600000L);
        ReflectionTestUtils.setField(authService, "jwtRefreshExpirationMs", 14400000L);
    }

    @Test
    void testLoginSuccess() {
        LoginRequest request = new LoginRequest();
        request.setEmail("admin@inmobot.com");
        request.setPassword("password123");

        UserEntity user = new UserEntity();
        user.setEmail("admin@inmobot.com");
        user.setPassword("encodedPassword");
        user.setEnabled(true);

        RoleEntity role = new RoleEntity();
        role.setName("ADMIN");
        user.setRoles(Set.of(role));

        when(userRepository.findByEmail("admin@inmobot.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);
        when(jwtTokenProvider.generateToken(any(), any(), any())).thenReturn("mockAccessToken");
        when(jwtTokenProvider.generateRefreshToken(any())).thenReturn("mockRefreshToken");

        JwtAuthResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("mockAccessToken", response.getAccessToken());
        assertEquals("mockRefreshToken", response.getRefreshToken());
        verify(tokenRepository, times(1)).save(any(TokenEntity.class));
    }

    @Test
    void testLoginInvalidPassword() {
        LoginRequest request = new LoginRequest();
        request.setEmail("admin@inmobot.com");
        request.setPassword("wrongPassword");

        UserEntity user = new UserEntity();
        user.setEmail("admin@inmobot.com");
        user.setPassword("encodedPassword");

        when(userRepository.findByEmail("admin@inmobot.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);

        DomainException exception = assertThrows(DomainException.class, () -> authService.login(request));
        assertEquals("Invalid email or password", exception.getMessage());
    }
}
