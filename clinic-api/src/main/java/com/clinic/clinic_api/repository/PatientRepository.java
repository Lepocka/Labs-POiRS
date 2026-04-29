package com.clinic.clinic_api.repository;

import com.clinic.clinic_api.model.Patient;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class PatientRepository {
    private final Map<Long, Patient> patients = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public List<Patient> findAll() {
        return new ArrayList<>(patients.values());
    }

    public Optional<Patient> findById(Long id) {
        return Optional.ofNullable(patients.get(id));
    }

    public Patient save(Patient Patient) {
        if (Patient.getId() == null) {
            Patient.setId(idGenerator.getAndIncrement());
        }
        patients.put(Patient.getId(), Patient);
        return Patient;
    }

    public void deleteById(Long id) {
        patients.remove(id);
    }
}