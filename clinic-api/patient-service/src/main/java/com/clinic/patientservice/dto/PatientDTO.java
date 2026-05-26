package com.clinic.patientservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;

@Data
public class PatientDTO {
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotBlank(message = "Ім'я пацієнта не може бути порожнім")
    @Size(min = 2, max = 100, message = "Ім'я має містити від 2 до 100 символів")
    private String name;

    @Pattern(regexp = "^\\+380\\d{9}$", message = "Номер телефону має бути у форматі +380XXXXXXXXX")
    private String phone;

    @NotNull(message = "Дата народження є обов'язковою")
    @Past(message = "Дата народження не може бути в майбутньому")
    private LocalDate birthDate;
}