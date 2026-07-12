package com.inmobot.properties.application.port.in;

import java.util.List;
import java.util.Optional;

import com.inmobot.properties.infrastructure.entity.PropertyEntity;

public interface ManagePropertyUseCase {
    List<PropertyEntity> findAll();

    Optional<PropertyEntity> findById(Long id);

    PropertyEntity save(PropertyEntity entity);

    void deleteById(Long id);
}
