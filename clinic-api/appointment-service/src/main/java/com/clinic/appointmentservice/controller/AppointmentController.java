package com.clinic.appointmentservice.controller;

import com.clinic.appointmentservice.dto.AppointmentDTO;
import com.clinic.appointmentservice.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor // Lombok сам згенерує конструктор
public class AppointmentController {

    private final AppointmentService appointmentService;

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
    @ResponseStatus(HttpStatus.NO_CONTENT)
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

    @GetMapping("/by-history/{historyId}")
    public ResponseEntity<List<AppointmentDTO>> getAppointmentsForHistory(@PathVariable Long historyId) {
        return ResponseEntity.ok(appointmentService.getAppointmentsByHistoryId(historyId));
    }

    // --- НОВИЙ ЕНДПОІНТ: Для перевірки перед видаленням пацієнта ---
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<AppointmentDTO>> getAppointmentsForPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(appointmentService.getByPatientId(patientId));
    }
}