package com.clinic.clinic_api.dto;

import com.clinic.clinic_api.model.enums.AppointmentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AppointmentDTO {
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotNull(message = "ID пацієнта обов'язкове")
    @Positive(message = "ID пацієнта має бути додатнім числом")
    private Long patientId;

    @NotNull(message = "ID лікаря обов'язкове")
    @Positive(message = "ID лікаря має бути додатнім числом")
    private Long doctorId;

    @NotNull(message = "Дата та час прийому обов'язкові")
    @FutureOrPresent(message = "Час прийому не може бути в минулому")
    private LocalDateTime dateTime;

    @Schema(description = "Статус запису на прийом", example = "SCHEDULED")
    private AppointmentStatus status;

    @Size(max = 255, message = "Причина звернення занадто довга (максимум 255 символів)")
    private String reason;
}