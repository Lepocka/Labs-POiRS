package com.clinic.patientservice.controller;

import com.clinic.patientservice.dto.TreatmentDTO;
import com.clinic.patientservice.service.TreatmentService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/treatments")
public class TreatmentController {
    private final TreatmentService treatmentService;
    public TreatmentController(TreatmentService treatmentService) {
        this.treatmentService = treatmentService;
    }

    @GetMapping
    public Page<TreatmentDTO> getAll(@PageableDefault(size = 10, sort = "medicineName") Pageable pageable) {
        return treatmentService.getAllTreatments(pageable);
    }

    @GetMapping("/{id}")
    public TreatmentDTO getById(@PathVariable Long id) {
        return treatmentService.getTreatmentById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TreatmentDTO create(@Valid @RequestBody TreatmentDTO treatmentDTO) {
        return treatmentService.createTreatment(treatmentDTO);
    }

    @PutMapping("/{id}")
    public TreatmentDTO update(@PathVariable Long id, @Valid @RequestBody TreatmentDTO treatmentDTO) {
        return treatmentService.updateTreatment(id, treatmentDTO);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT) // Повертає 204 статус (успішно, без тіла відповіді)
    public void delete(@PathVariable Long id) {
        treatmentService.deleteTreatment(id);
    }
}