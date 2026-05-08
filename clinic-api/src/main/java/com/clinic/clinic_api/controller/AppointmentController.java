package com.clinic.clinic_api.controller;

import com.clinic.clinic_api.dto.AppointmentDTO;
import com.clinic.clinic_api.service.AppointmentService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @GetMapping
    public Page<AppointmentDTO> getAll(@PageableDefault(size = 10, sort = "dateTime") Pageable pageable) {
        return appointmentService.getAllAppointments(pageable);
    }

    @GetMapping("/{id}")
    public AppointmentDTO getById(@PathVariable Long id) {
        return appointmentService.getAppointmentById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AppointmentDTO create(@Valid @RequestBody AppointmentDTO appointment) {
        return appointmentService.createAppointment(appointment);
    }

    @PutMapping("/{id}")
    public AppointmentDTO update(@PathVariable Long id, @Valid @RequestBody AppointmentDTO appointmentDTO) {
        return appointmentService.updateAppointment(id, appointmentDTO);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT) // Повертає 204 статус (успішно, без тіла відповіді)
    public void delete(@PathVariable Long id) {
        appointmentService.deleteAppointment(id);
    }

    @PatchMapping("/{id}/complete")
    public AppointmentDTO completeAppointment(@PathVariable Long id) {
        return appointmentService.completeAppointment(id);
    }

    @PatchMapping("/{id}/cancel")
    public AppointmentDTO cancelAppointment(@PathVariable Long id) {
        return appointmentService.cancelAppointment(id);
    }
}