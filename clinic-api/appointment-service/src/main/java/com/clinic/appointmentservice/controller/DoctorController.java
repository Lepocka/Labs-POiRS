package com.clinic.appointmentservice.controller;

import com.clinic.appointmentservice.dto.DoctorDTO;
import com.clinic.appointmentservice.service.DoctorService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/doctors")
public class DoctorController {
    private final DoctorService doctorService;

    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @GetMapping
    public Page<DoctorDTO> getAll(@PageableDefault(size = 10, sort = "name") Pageable pageable) {
        return doctorService.getAllDoctors(pageable);
    }

    @GetMapping("/{id}")
    public DoctorDTO getById(@PathVariable Long id) {
        return doctorService.getDoctorById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DoctorDTO create(@Valid @RequestBody DoctorDTO doctor) {
        return doctorService.createDoctor(doctor);
    }

    @PutMapping("/{id}")
    public DoctorDTO update(@PathVariable Long id, @Valid @RequestBody DoctorDTO doctorDTO) {
        return doctorService.updateDoctor(id, doctorDTO);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT) // Повертає 204 статус (успішно, без тіла відповіді)
    public void delete(@PathVariable Long id) {
        doctorService.deleteDoctor(id);
    }
}
