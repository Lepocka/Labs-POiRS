package com.clinic.clinic_api.controller;

import com.clinic.clinic_api.dto.TreatmentDTO;
import com.clinic.clinic_api.service.TreatmentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/treatments")
public class TreatmentController {
    private final TreatmentService treatmentService;
    public TreatmentController(TreatmentService treatmentService) { this.treatmentService = treatmentService; }

    @GetMapping
    public List<TreatmentDTO> getAll() { return treatmentService.getAllTreatments(); }

    @GetMapping("/{id}")
    public TreatmentDTO getById(@PathVariable Long id) {
        return treatmentService.getTreatmentById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TreatmentDTO create(@Valid @RequestBody TreatmentDTO treatment) {
        return treatmentService.createTreatment(treatment);
    }
}