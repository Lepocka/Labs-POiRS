package com.clinic.clinic_api.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class MedicalHistory extends BaseModel{
    private Long patientId;
    private List<Long> appointmentIds;
    private String bloodType;
}