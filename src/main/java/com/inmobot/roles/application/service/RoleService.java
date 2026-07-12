package com.inmobot.roles.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.inmobot.roles.application.port.in.ManageRoleUseCase;
import com.inmobot.roles.infrastructure.entity.RoleEntity;
import com.inmobot.roles.infrastructure.repository.RoleRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoleService implements ManageRoleUseCase {

    private final RoleRepository roleRepository;

    @Override
    public List<RoleEntity> findAll() {
        return roleRepository.findAll();
    }

    @Override
    public Optional<RoleEntity> findById(Long id) {
        return roleRepository.findById(id);
    }

    @Override
    public RoleEntity save(RoleEntity entity) {
        return roleRepository.save(entity);
    }

    @Override
    public void deleteById(Long id) {
        roleRepository.deleteById(id);
    }
}
