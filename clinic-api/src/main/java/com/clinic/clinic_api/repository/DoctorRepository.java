package com.clinic.clinic_api.repository;

import com.clinic.clinic_api.model.Doctor;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class DoctorRepository {
    private final Map<Long, Doctor> doctors = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public List<Doctor> findAll() {
        return new ArrayList<>(doctors.values());
    }

    public Optional<Doctor> findById(Long id) {
        return Optional.ofNullable(doctors.get(id));
    }

    public Doctor save(Doctor doctor) {
        if (doctor.getId() == null) {
            doctor.setId(idGenerator.getAndIncrement());
        }
        doctors.put(doctor.getId(), doctor);
        return doctor;
    }

    public void deleteById(Long id) {
        doctors.remove(id);
    }
}