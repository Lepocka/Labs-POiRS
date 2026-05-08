package com.clinic.clinic_api.repository;

import com.clinic.clinic_api.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    boolean existsByPhone(String phone);
}