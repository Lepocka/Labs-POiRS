package com.clinic.patientservice.controller;

import com.clinic.patientservice.dto.PatientDTO;
import com.clinic.patientservice.service.PatientService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/patients")
public class PatientController {
    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @GetMapping
    public Page<PatientDTO> getAll(@PageableDefault(size = 10, sort = "name") Pageable pageable) {
        return patientService.getAllPatients(pageable);
    }

    @GetMapping("/{id}")
    public PatientDTO getById(@PathVariable Long id) {
        return patientService.getPatientById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PatientDTO create(@Valid @RequestBody PatientDTO patientDTO) {
        return patientService.createPatient(patientDTO);
    }

    @PutMapping("/{id}")
    public PatientDTO update(@PathVariable Long id, @Valid @RequestBody PatientDTO patientDTO) {
        return patientService.updatePatient(id, patientDTO);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT) // Повертає 204 статус (успішно, без тіла відповіді)
    public void delete(@PathVariable Long id) {
        patientService.deletePatient(id);
    }
}
