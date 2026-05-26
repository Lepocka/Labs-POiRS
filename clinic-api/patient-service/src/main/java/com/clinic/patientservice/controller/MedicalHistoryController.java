package com.clinic.patientservice.controller;

import com.clinic.patientservice.dto.MedicalHistoryDTO;
import com.clinic.patientservice.service.MedicalHistoryService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/medical-histories")
public class MedicalHistoryController {
    private final MedicalHistoryService historyService;
    public MedicalHistoryController(MedicalHistoryService historyService) {
        this.historyService = historyService;
    }

    @GetMapping
    public Page<MedicalHistoryDTO> getAll(@PageableDefault(size = 10, sort = "id") Pageable pageable) {
        return historyService.getAllMedicalHistories(pageable);
    }

    @GetMapping("/{id}")
    public MedicalHistoryDTO getById(@PathVariable Long id) {
        return historyService.getMedicalHistoryById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MedicalHistoryDTO create(@Valid @RequestBody MedicalHistoryDTO history) {
        return historyService.createMedicalHistory(history);
    }

    @PutMapping("/{id}")
    public MedicalHistoryDTO update(@PathVariable Long id, @Valid @RequestBody MedicalHistoryDTO historyDTO) {
        return historyService.updateMedicalHistory(id, historyDTO);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT) // Повертає 204 статус (успішно, без тіла відповіді)
    public void delete(@PathVariable Long id) {
        historyService.deleteMedicalHistory(id);
    }

    @GetMapping("/patient/{patientId}")
    public MedicalHistoryDTO getHistoryByPatientId(@PathVariable Long patientId) {
        return historyService.getByPatientId(patientId);
    }
}