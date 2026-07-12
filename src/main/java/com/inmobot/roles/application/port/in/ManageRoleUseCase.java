package com.inmobot.roles.application.port.in;

import java.util.List;
import java.util.Optional;

import com.inmobot.roles.infrastructure.entity.RoleEntity;

public interface ManageRoleUseCase {
    List<RoleEntity> findAll();

    Optional<RoleEntity> findById(Long id);

    RoleEntity save(RoleEntity entity);

    void deleteById(Long id);
}
