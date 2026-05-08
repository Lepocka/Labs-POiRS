package com.clinic.clinic_api.repository;

import com.clinic.clinic_api.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {

}