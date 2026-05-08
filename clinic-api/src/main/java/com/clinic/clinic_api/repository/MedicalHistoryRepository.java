package com.clinic.clinic_api.repository;

import com.clinic.clinic_api.model.MedicalHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface MedicalHistoryRepository extends JpaRepository<MedicalHistory, Long> {
    boolean existsByPatientId(Long patientId);

    Optional<MedicalHistory> findByPatientId(Long patientId);

}