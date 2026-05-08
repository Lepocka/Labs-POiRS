package com.clinic.clinic_api.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "doctors")
@Data
@EqualsAndHashCode(callSuper = true)
public class Doctor extends BaseModel{
    private String name;
    private String specialization;
    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL)
    private List<Appointment> appointments = new ArrayList<>();
}