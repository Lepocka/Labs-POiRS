package com.clinic.clinic_api.repository;

import com.clinic.clinic_api.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;


@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    boolean existsByDoctorIdAndDateTime(Long doctorId, LocalDateTime dateTime);
}