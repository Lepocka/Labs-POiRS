package com.clinic.clinic_api.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "patients")
@Data
@EqualsAndHashCode(callSuper = true)
public class Patient extends BaseModel{
    private String name;
    private LocalDate birthDate;
    private String phone;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL)
    private List<Appointment> appointments = new ArrayList<>();


}
