package com.clinic.patientservice.model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.persistence.*;

@Entity
@Table(name = "treatments")
@Data
@EqualsAndHashCode(callSuper = true)
public class Treatment extends BaseModel{
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diagnosis_id")
    private Diagnosis diagnosis;
    private String medicineName;
    private String dosage;
    private int durationDays;
}