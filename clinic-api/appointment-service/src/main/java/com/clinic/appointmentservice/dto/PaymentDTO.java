package com.clinic.appointmentservice.dto;

import com.clinic.appointmentservice.model.enums.PaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class PaymentDTO {
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotNull(message = "ID прийому обов'язкове")
    @Positive(message = "ID прийому має бути додатнім числом")
    private Long appointmentId;

    @NotNull(message = "Сума оплати обов'язкова")
    @Positive(message = "Сума оплати має бути більшою за нуль")
    private BigDecimal amount;

    @Schema(description = "Статус оплати", example = "PENDING")
    private PaymentStatus status;
}