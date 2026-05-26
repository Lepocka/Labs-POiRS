package com.clinic.patientservice.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.persistence.*;

@Entity
@Table(name = "medical_histories")
@Data
@EqualsAndHashCode(callSuper = true)
public class MedicalHistory extends BaseModel{
    @OneToOne
    @JoinColumn(name = "patient_id")
    private Patient patient;

    private String bloodType;
    private String chronicConditions;
}