package com.clinic.patientservice.repository;

import com.clinic.patientservice.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    boolean existsByPhone(String phone);
}