package com.clinic.clinic_api.model;

import com.clinic.clinic_api.model.enums.AppointmentStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "appointments")
@Data
@EqualsAndHashCode(callSuper = true)
public class Appointment extends BaseModel{
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id")
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;

    @OneToMany(mappedBy = "appointment", cascade = CascadeType.ALL)
    private List<Diagnosis> diagnoses = new ArrayList<>();

    @OneToOne(mappedBy = "appointment", cascade = CascadeType.ALL)
    private Payment payment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medical_history_id")
    private MedicalHistory medicalHistory;

    private LocalDateTime dateTime;

    @Enumerated(EnumType.STRING) // Обов'язково!
    private AppointmentStatus status;
    private String reason;
}