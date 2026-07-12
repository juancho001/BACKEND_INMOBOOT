package com.inmobot.users.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.inmobot.users.application.port.in.ManageUserUseCase;
import com.inmobot.users.infrastructure.entity.UserEntity;
import com.inmobot.users.infrastructure.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements ManageUserUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<UserEntity> findAll() {
        return userRepository.findAll();
    }

    @Override
    public Optional<UserEntity> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public UserEntity save(UserEntity entity) {
        if (entity.getId() == null) {
            // Encode password on creation
            entity.setPassword(passwordEncoder.encode(entity.getPassword()));
        }
        return userRepository.save(entity);
    }

    @Override
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }
}
