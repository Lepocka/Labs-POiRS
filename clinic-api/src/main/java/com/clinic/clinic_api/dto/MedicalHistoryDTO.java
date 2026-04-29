package com.clinic.clinic_api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class MedicalHistoryDTO {
    private Long id;

    @NotNull(message = "Patient ID is required")
    private Long patientId;

    private List<Long> appointmentIds;

    private String bloodType;
}