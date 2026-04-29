package com.clinic.clinic_api.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
public class Payment extends BaseModel{
    private Long appointmentId;
    private BigDecimal amount;
    private String status;
}