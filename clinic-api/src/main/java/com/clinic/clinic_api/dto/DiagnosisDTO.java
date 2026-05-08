package com.clinic.clinic_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class DiagnosisDTO {
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotNull(message = "ID прийому обов'язкове")
    @Positive(message = "ID прийому має бути додатнім числом")
    private Long appointmentId;

    @NotBlank(message = "Опис діагнозу не може бути порожнім")
    private String description;

    @NotBlank(message = "Код МКХ (ICD) є обов'язковим")
    @Pattern(regexp = "^[A-Z][0-9]{2}(\\.[0-9]{1,2})?$", message = "Невірний формат коду МКХ (приклад: J01.9)")
    private String ICDCode;
}