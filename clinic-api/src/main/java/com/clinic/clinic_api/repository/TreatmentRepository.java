package com.clinic.clinic_api.repository;

import com.clinic.clinic_api.model.Treatment;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class TreatmentRepository {
    // Внутрішнє сховище (імітація бази даних)
    private final Map<Long, Treatment> treatments = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public List<Treatment> findAll() {
        return new ArrayList<>(treatments.values());
    }

    public Optional<Treatment> findById(Long id) {
        return Optional.ofNullable(treatments.get(id));
    }

    public Treatment save(Treatment Treatment) {
        if (Treatment.getId() == null) {
            Treatment.setId(idGenerator.getAndIncrement());
        }
        treatments.put(Treatment.getId(), Treatment);
        return Treatment;
    }

    public void deleteById(Long id) {
        treatments.remove(id);
    }
}