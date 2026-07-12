package com.inmobot.tickets.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.inmobot.tickets.application.port.in.ManageTicketUseCase;
import com.inmobot.tickets.infrastructure.entity.TicketEntity;
import com.inmobot.tickets.infrastructure.repository.TicketRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TicketService implements ManageTicketUseCase {

    private final TicketRepository repository;

    @Override
    public List<TicketEntity> findAll() {
        return repository.findAll();
    }

    @Override
    public Optional<TicketEntity> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public TicketEntity save(TicketEntity entity) {
        return repository.save(entity);
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}
