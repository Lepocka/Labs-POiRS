package com.clinic.clinic_api.repository;

import com.clinic.clinic_api.model.Diagnosis;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class DiagnosisRepository {
    private final Map<Long, Diagnosis> diagnoses = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public List<Diagnosis> findAll() {
        return new ArrayList<>(diagnoses.values());
    }

    public Optional<Diagnosis> findById(Long id) {
        return Optional.ofNullable(diagnoses.get(id));
    }

    public Diagnosis save(Diagnosis diagnosis) {
        if (diagnosis.getId() == null) {
            diagnosis.setId(idGenerator.getAndIncrement());
        }
        diagnoses.put(diagnosis.getId(), diagnosis);
        return diagnosis;
    }

    public void deleteById(Long id) {
        diagnoses.remove(id);
    }
}