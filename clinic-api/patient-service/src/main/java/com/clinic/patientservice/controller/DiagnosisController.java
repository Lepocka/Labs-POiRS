package com.clinic.patientservice.controller;

import com.clinic.patientservice.dto.DiagnosisDTO;
import com.clinic.patientservice.service.DiagnosisService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/diagnoses")
@RequiredArgsConstructor // Lombok сам згенерує конструктор
public class DiagnosisController {

    private final DiagnosisService diagnosisService;

    @GetMapping
    public Page<DiagnosisDTO> getAll(@PageableDefault(size = 10, sort = "id") Pageable pageable) {
        return diagnosisService.getAllDiagnoses(pageable);
    }

    @GetMapping("/{id}")
    public DiagnosisDTO getById(@PathVariable Long id) {
        return diagnosisService.getDiagnosisById(id);
    }

    // --- НОВИЙ ЕНДПОІНТ: Спеціально для Feign-клієнта з appointment-service! ---
    @GetMapping("/appointment/{appointmentId}")
    public List<DiagnosisDTO> getByAppointment(@PathVariable Long appointmentId) {
        return diagnosisService.getByAppointmentId(appointmentId);
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
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        diagnosisService.deleteDiagnosis(id);
    }
}