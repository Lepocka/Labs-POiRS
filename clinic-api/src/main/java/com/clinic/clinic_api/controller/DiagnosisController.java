package com.clinic.clinic_api.controller;

import com.clinic.clinic_api.dto.DiagnosisDTO;
import com.clinic.clinic_api.service.DiagnosisService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/diagnoses")
public class DiagnosisController {
    private final DiagnosisService diagnosisService;
    public DiagnosisController(DiagnosisService diagnosisService) { this.diagnosisService = diagnosisService; }

    @GetMapping
    public List<DiagnosisDTO> getAll() { return diagnosisService.getAllDiagnoses(); }

    @GetMapping("/{id}")
    public DiagnosisDTO getById(@PathVariable Long id) {
        return diagnosisService.getDiagnosisById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DiagnosisDTO create(@Valid @RequestBody DiagnosisDTO diagnosis) {
        return diagnosisService.createDiagnosis(diagnosis);
    }
}