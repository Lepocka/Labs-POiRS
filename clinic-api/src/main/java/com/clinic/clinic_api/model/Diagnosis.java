package com.clinic.clinic_api.model;

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
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id")
    private Appointment appointment;

    @OneToMany(mappedBy = "diagnosis", cascade = CascadeType.ALL)
    private List<Treatment> treatments = new ArrayList<>();
    private String description;
    private String ICDCode;
}