package com.clinic.patientservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.util.List;

@Data
public class MedicalHistoryDTO {
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotNull(message = "ID пацієнта обов'язкове")
    @Positive(message = "ID пацієнта має бути додатнім числом")
    private Long patientId;

    private List<Long> appointmentIds;

    @NotBlank(message = "Група крові обов'язкова")
    @Pattern(regexp = "^(A|B|AB|O)[+-]$", message = "Невірний формат групи крові (допустимі: A+, O-, AB+ тощо)")
    private String bloodType;
}