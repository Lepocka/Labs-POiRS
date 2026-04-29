package com.clinic.clinic_api.model;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Treatment extends BaseModel{
    private Long diagnosisId;
    private String medicineName;
    private String dosage;
    private int durationDays;
}