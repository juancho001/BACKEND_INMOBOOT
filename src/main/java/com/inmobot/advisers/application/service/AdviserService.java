package com.inmobot.advisers.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.inmobot.advisers.application.port.in.ManageAdviserUseCase;
import com.inmobot.advisers.infrastructure.entity.AdviserEntity;
import com.inmobot.advisers.infrastructure.repository.AdviserRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdviserService implements ManageAdviserUseCase {

    private final AdviserRepository repository;

    @Override
    public List<AdviserEntity> findAll() {
        return repository.findAll();
    }

    @Override
    public Optional<AdviserEntity> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public AdviserEntity save(AdviserEntity entity) {
        return repository.save(entity);
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}
