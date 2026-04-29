package com.clinic.clinic_api.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
public class Appointment extends BaseModel{
    private Long patientId;
    private Long doctorId;
    private LocalDateTime dateTime;
    private String status;
    private String reason;
}