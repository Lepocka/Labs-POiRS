package com.clinic.clinic_api.model;

import lombok.Data;
import jakarta.persistence.*;

@Data
@MappedSuperclass
public class BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;
}
