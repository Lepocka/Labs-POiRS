package com.clinic.patientservice.repository;

import com.clinic.patientservice.model.Diagnosis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface DiagnosisRepository extends JpaRepository<Diagnosis, Long> {
    List<Diagnosis> findAllByAppointmentId(Long appointmentId);

}