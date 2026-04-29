package com.clinic.clinic_api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DiagnosisDTO {
    private Long id;

    @NotNull(message = "Appointment ID is required")
    private Long appointmentId;

    @NotBlank(message = "Description is required")
    private String description;

    @NotBlank(message = "ICD code is required")
    private String ICDCode;
}