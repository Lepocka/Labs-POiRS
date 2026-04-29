package com.clinic.clinic_api.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
public class Patient extends BaseModel{
    private String name;
    private LocalDate birthDate;
    private String phone;
}
