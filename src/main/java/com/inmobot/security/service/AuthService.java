package com.inmobot.security.service;

import com.inmobot.common.exception.DomainException;
import com.inmobot.security.infrastructure.entity.TokenEntity;
import com.inmobot.security.infrastructure.repository.TokenRepository;
import com.inmobot.security.jwt.JwtTokenProvider;
import com.inmobot.security.model.JwtAuthResponse;
import com.inmobot.security.model.LoginRequest;
import com.inmobot.security.model.RefreshTokenRequest;
import com.inmobot.users.infrastructure.entity.UserEntity;
import com.inmobot.users.infrastructure.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    @Value("${jwt.expiration}")
    private long jwtExpirationMs;

    @Value("${jwt.refresh-expiration}")
    private long jwtRefreshExpirationMs;

    @Transactional
    public JwtAuthResponse login(LoginRequest loginRequest) {
        UserEntity user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new DomainException("Invalid email or password"));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new DomainException("Invalid email or password");
        }

        if (!user.getEnabled()) {
            throw new DomainException("User account is disabled");
        }

        List<String> roles = user.getRoles().stream()
                .map(role -> role.getName())
                .collect(Collectors.toList());

        String name = user.getEmail(); // Could be enhanced using tb_detalle_usuario
        String accessToken = jwtTokenProvider.generateToken(user.getEmail(), name, roles);
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getEmail());

        saveUserToken(user, refreshToken);

        return JwtAuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Transactional
    public JwtAuthResponse refreshToken(RefreshTokenRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        if (!jwtTokenProvider.validateToken(requestRefreshToken)) {
            throw new DomainException("Refresh token is invalid or expired. Please login again.");
        }

        TokenEntity tokenEntity = tokenRepository.findByToken(requestRefreshToken)
                .orElseThrow(() -> new DomainException("Refresh token not found in database"));

        if (tokenEntity.getExpiresAt().isBefore(LocalDateTime.now())) {
            tokenEntity.setStatus("EXPIRED");
            tokenRepository.save(tokenEntity);
            throw new DomainException("Refresh token is expired. Please login again.");
        }

        UserEntity user = tokenEntity.getUser();
        List<String> roles = user.getRoles().stream()
                .map(role -> role.getName())
                .collect(Collectors.toList());

        String name = user.getEmail();
        String newAccessToken = jwtTokenProvider.generateToken(user.getEmail(), name, roles);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(user.getEmail());

        tokenEntity.setStatus("REFRESHED");
        tokenEntity.setRefreshedAt(LocalDateTime.now());
        tokenRepository.save(tokenEntity);

        saveUserToken(user, newRefreshToken);

        return JwtAuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }

    private void saveUserToken(UserEntity user, String tokenStr) {
        TokenEntity tokenEntity = new TokenEntity();
        tokenEntity.setUser(user);
        tokenEntity.setToken(tokenStr);
        tokenEntity.setStatus("ACTIVE");
        tokenEntity.setIssuedAt(LocalDateTime.now());
        tokenEntity
                .setExpiresAt(LocalDateTime.now().plus(jwtRefreshExpirationMs, java.time.temporal.ChronoUnit.MILLIS));
        tokenEntity.setLastActivityAt(LocalDateTime.now());
        tokenRepository.save(tokenEntity);
    }
}
