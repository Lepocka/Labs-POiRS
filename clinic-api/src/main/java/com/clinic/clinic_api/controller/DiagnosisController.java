package com.clinic.clinic_api.controller;

import com.clinic.clinic_api.dto.AppointmentDTO;
import com.clinic.clinic_api.dto.DiagnosisDTO;
import com.clinic.clinic_api.service.DiagnosisService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/diagnoses")
public class DiagnosisController {
    private final DiagnosisService diagnosisService;
    public DiagnosisController(DiagnosisService diagnosisService) {
        this.diagnosisService = diagnosisService;
    }

    @GetMapping
    public Page<DiagnosisDTO> getAll(@PageableDefault(size = 10, sort = "id") Pageable pageable) {
        return diagnosisService.getAllDiagnoses(pageable);
    }

    @GetMapping("/{id}")
    public DiagnosisDTO getById(@PathVariable Long id) {
        return diagnosisService.getDiagnosisById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DiagnosisDTO create(@Valid @RequestBody DiagnosisDTO diagnosis) {
        return diagnosisService.createDiagnosis(diagnosis);
    }

    @PutMapping("/{id}")
    public DiagnosisDTO update(@PathVariable Long id, @Valid @RequestBody DiagnosisDTO diagnosisDTO) {
        return diagnosisService.updateDiagnosis(id, diagnosisDTO);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT) // Повертає 204 статус (успішно, без тіла відповіді)
    public void delete(@PathVariable Long id) {
        diagnosisService.deleteDiagnosis(id);
    }
}