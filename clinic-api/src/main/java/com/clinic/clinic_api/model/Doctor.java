package com.clinic.clinic_api.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Doctor extends BaseModel{
    private String name;
    private String specialization;
}