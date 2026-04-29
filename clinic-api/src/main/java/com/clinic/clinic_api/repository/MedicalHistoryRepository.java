package com.clinic.clinic_api.repository;

import com.clinic.clinic_api.model.MedicalHistory;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class MedicalHistoryRepository {
    private final Map<Long, MedicalHistory> medicalHistories = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public List<MedicalHistory> findAll() {
        return new ArrayList<>(medicalHistories.values());
    }

    public Optional<MedicalHistory> findById(Long id) {
        return Optional.ofNullable(medicalHistories.get(id));
    }

    public MedicalHistory save(MedicalHistory MedicalHistory) {
        if (MedicalHistory.getId() == null) {
            MedicalHistory.setId(idGenerator.getAndIncrement());
        }
        medicalHistories.put(MedicalHistory.getId(), MedicalHistory);
        return MedicalHistory;
    }

    public void deleteById(Long id) {
        medicalHistories.remove(id);
    }
}