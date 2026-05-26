package com.clinic.patientservice.repository;

import com.clinic.patientservice.model.Treatment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface TreatmentRepository extends JpaRepository<Treatment, Long> {

}