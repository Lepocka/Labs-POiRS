package com.clinic.clinic_api.controller;

import com.clinic.clinic_api.dto.MedicalHistoryDTO;
import com.clinic.clinic_api.service.MedicalHistoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/medical-histories")
public class MedicalHistoryController {
    private final MedicalHistoryService historyService;
    public MedicalHistoryController(MedicalHistoryService historyService) { this.historyService = historyService; }

    @GetMapping
    public List<MedicalHistoryDTO> getAll() { return historyService.getAllMedicalHistories(); }

    @GetMapping("/{id}")
    public MedicalHistoryDTO getById(@PathVariable Long id) {
        return historyService.getMedicalHistoryById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MedicalHistoryDTO create(@Valid @RequestBody MedicalHistoryDTO history) {
        return historyService.createMedicalHistory(history);
    }
}