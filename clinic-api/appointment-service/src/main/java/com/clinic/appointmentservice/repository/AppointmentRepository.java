package com.clinic.appointmentservice.repository;

import com.clinic.appointmentservice.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    boolean existsByDoctorIdAndDateTime(Long doctorId, LocalDateTime dateTime);
    List<Appointment> findAllByMedicalHistoryId(Long medicalHistoryId);
    List<Appointment> findAllByPatientId(Long patientId);
}