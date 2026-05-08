package com.clinic.clinic_api.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.*;

@Entity
@Table(name = "medical_histories")
@Data
@EqualsAndHashCode(callSuper = true)
public class MedicalHistory extends BaseModel{
    @OneToOne
    @JoinColumn(name = "patient_id")
    private Patient patient;

    // Список усіх прийомів для швидкого доступу до історії
    @OneToMany(mappedBy = "medicalHistory", cascade = CascadeType.ALL)
    private List<Appointment> historyRecords = new ArrayList<>();
    private String bloodType;
    private String chronicConditions;
}