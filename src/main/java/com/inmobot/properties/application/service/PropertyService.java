package com.inmobot.properties.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.inmobot.properties.application.port.in.ManagePropertyUseCase;
import com.inmobot.properties.infrastructure.entity.PropertyEntity;
import com.inmobot.properties.infrastructure.repository.PropertyRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PropertyService implements ManagePropertyUseCase {

    private final PropertyRepository repository;

    @Override
    public List<PropertyEntity> findAll() {
        return repository.findAll();
    }

    @Override
    public Optional<PropertyEntity> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public PropertyEntity save(PropertyEntity entity) {
        return repository.save(entity);
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}
