package com.clinic.clinic_api.model;

import com.clinic.clinic_api.model.enums.PaymentStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import jakarta.persistence.*;

@Entity
@Table(name = "payments")
@Data
@EqualsAndHashCode(callSuper = true)
public class Payment extends BaseModel{
    @OneToOne
    @JoinColumn(name = "appointment_id")
    private Appointment appointment;
    private BigDecimal amount;
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;
}