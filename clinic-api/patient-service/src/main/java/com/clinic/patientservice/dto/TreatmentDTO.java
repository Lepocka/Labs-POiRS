package com.clinic.patientservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class TreatmentDTO {
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotNull(message = "ID діагнозу обов'язкове")
    @Positive(message = "ID діагнозу має бути додатнім числом")
    private Long diagnosisId;

    @NotBlank(message = "Назва ліків не може бути порожньою")
    private String medicineName;

    @NotBlank(message = "Дозування є обов'язковим")
    private String dosage;

    @NotNull(message = "Тривалість лікування обов'язкова")
    @Min(value = 1, message = "Мінімальна тривалість лікування - 1 день")
    @Max(value = 365, message = "Максимальна тривалість лікування - 365 днів")
    private Integer durationDays;
}