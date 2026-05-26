package com.clinic.patientservice.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

import jakarta.persistence.*;

@Entity
@Table(name = "patients")
@Data
@EqualsAndHashCode(callSuper = true)
public class Patient extends BaseModel{
    private String name;
    private LocalDate birthDate;
    private String phone;
}
