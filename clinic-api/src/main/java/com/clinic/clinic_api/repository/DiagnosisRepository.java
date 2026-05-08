package com.clinic.clinic_api.repository;

import com.clinic.clinic_api.model.Diagnosis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface DiagnosisRepository extends JpaRepository<Diagnosis, Long> {

}