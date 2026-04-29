package com.clinic.clinic_api.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Diagnosis extends BaseModel{
    private Long appointmentId;
    private String description;
    private String ICDCode;
}