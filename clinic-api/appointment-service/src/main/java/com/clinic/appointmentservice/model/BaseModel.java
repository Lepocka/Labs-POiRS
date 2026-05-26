package com.clinic.appointmentservice.model;

import lombok.Data;
import jakarta.persistence.*;

@Data
@MappedSuperclass
public class BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;
}
