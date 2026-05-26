package com.clinic.appointmentservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class DoctorDTO {
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotBlank(message = "Ім'я лікаря не може бути порожнім")
    @Size(min = 2, max = 100, message = "Ім'я має містити від 2 до 100 символів")
    private String name;

    @NotBlank(message = "Спеціалізація є обов'язковою")
    private String specialization;
}