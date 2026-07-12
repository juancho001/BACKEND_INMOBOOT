package com.inmobot.advisers.application.port.in;

import java.util.List;
import java.util.Optional;

import com.inmobot.advisers.infrastructure.entity.AdviserEntity;

public interface ManageAdviserUseCase {

    List<AdviserEntity> findAll();

    Optional<AdviserEntity> findById(Long id);

    AdviserEntity save(AdviserEntity entity);
    void deleteById(Long id);
}
