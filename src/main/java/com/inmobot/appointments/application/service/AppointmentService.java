package com.inmobot.appointments.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.inmobot.appointments.application.port.in.ManageAppointmentUseCase;
import com.inmobot.appointments.infrastructure.entity.AppointmentEntity;
import com.inmobot.appointments.infrastructure.repository.AppointmentRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AppointmentService implements ManageAppointmentUseCase {

    private final AppointmentRepository repository;

    @Override
    public List<AppointmentEntity> findAll() {
        return repository.findAll();
    }

    @Override
    public Optional<AppointmentEntity> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public AppointmentEntity save(AppointmentEntity entity) {
        return repository.save(entity);
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}
