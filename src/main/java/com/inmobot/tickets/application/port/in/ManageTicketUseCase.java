package com.inmobot.tickets.application.port.in;

import java.util.List;
import java.util.Optional;

import com.inmobot.tickets.infrastructure.entity.TicketEntity;

public interface ManageTicketUseCase {
    List<TicketEntity> findAll();

    Optional<TicketEntity> findById(Long id);

    TicketEntity save(TicketEntity entity);

    void deleteById(Long id);
}
