package com.inmobot.appointments.application.port.in;

import java.util.List;
import java.util.Optional;

import com.inmobot.appointments.infrastructure.entity.AppointmentEntity;

public interface ManageAppointmentUseCase {
    List<AppointmentEntity> findAll();

    Optional<AppointmentEntity> findById(Long id);

    AppointmentEntity save(AppointmentEntity entity);

    void deleteById(Long id);
}
