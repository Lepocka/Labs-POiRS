package com.clinic.patientservice.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "diagnoses")
@Data
@EqualsAndHashCode(callSuper = true)
public class Diagnosis extends BaseModel{
    // Замість об'єкта Appointment зберігаємо лише його ID
    @Column(name = "appointment_id", nullable = false)
    private Long appointmentId;

    @OneToMany(mappedBy = "diagnosis", cascade = CascadeType.ALL)
    private List<Treatment> treatments = new ArrayList<>();
    private String description;
    private String ICDCode;
}