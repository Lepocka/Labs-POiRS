package com.clinic.clinic_api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TreatmentDTO {
    private Long id;

    @NotNull(message = "Diagnosis ID is required")
    private Long diagnosisId;

    @NotBlank(message = "Medicine name is required")
    private String medicineName;

    @NotBlank(message = "Dosage is required")
    private String dosage;

    @NotNull(message = "Duration in days is required")
    private Integer durationDays;
}