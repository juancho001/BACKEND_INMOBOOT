package com.inmobot.users.application.port.in;

import java.util.List;
import java.util.Optional;

import com.inmobot.users.infrastructure.entity.UserEntity;

public interface ManageUserUseCase {
    List<UserEntity> findAll();

    Optional<UserEntity> findById(Long id);

    UserEntity save(UserEntity entity);

    void deleteById(Long id);
}
