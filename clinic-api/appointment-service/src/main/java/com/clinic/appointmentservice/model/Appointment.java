package com.clinic.appointmentservice.model;

import com.clinic.appointmentservice.model.enums.AppointmentStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "appointments")
@Data
@EqualsAndHashCode(callSuper = true)
public class Appointment extends BaseModel{
    // Пацієнт тепер в іншому мікросервісі. Зберігаємо тільки його ID.
    @Column(name = "patient_id", nullable = false)
    private Long patientId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;

    @OneToOne(mappedBy = "appointment", cascade = CascadeType.ALL)
    private Payment payment;

    // Медична картка в іншому мікросервісі. Зберігаємо тільки її ID.
    @Column(name = "medical_history_id")
    private Long medicalHistoryId;

    private LocalDateTime dateTime;

    @Enumerated(EnumType.STRING) // Обов'язково!
    private AppointmentStatus status;
    private String reason;
}