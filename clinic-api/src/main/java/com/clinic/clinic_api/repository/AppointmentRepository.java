package com.clinic.clinic_api.repository;

import com.clinic.clinic_api.model.Appointment;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class AppointmentRepository {
    private final Map<Long, Appointment> appointments = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public List<Appointment> findAll() {
        return new ArrayList<>(appointments.values());
    }

    public Optional<Appointment> findById(Long id) {
        return Optional.ofNullable(appointments.get(id));
    }

    public Appointment save(Appointment Appointment) {
        if (Appointment.getId() == null) {
            Appointment.setId(idGenerator.getAndIncrement());
        }
        appointments.put(Appointment.getId(), Appointment);
        return Appointment;
    }

    public void deleteById(Long id) {
        appointments.remove(id);
    }
}