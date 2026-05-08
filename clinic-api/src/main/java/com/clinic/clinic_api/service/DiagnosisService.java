package com.clinic.clinic_api.service;

import com.clinic.clinic_api.dto.DiagnosisDTO;
import com.clinic.clinic_api.exception.ResourceNotFoundException;
import com.clinic.clinic_api.model.Appointment;
import com.clinic.clinic_api.model.Diagnosis;
import com.clinic.clinic_api.repository.AppointmentRepository;
import com.clinic.clinic_api.repository.DiagnosisRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class DiagnosisService {
    private final DiagnosisRepository diagnosisRepository;
    private final AppointmentRepository appointmentRepository;

    public DiagnosisService(DiagnosisRepository diagnosisRepository,
                            AppointmentRepository appointmentRepository) {
        this.diagnosisRepository = diagnosisRepository;
        this.appointmentRepository = appointmentRepository;
    }

    // --- Мапінг ---

    private DiagnosisDTO mapToDTO(Diagnosis diagnosis) {
        DiagnosisDTO dto = new DiagnosisDTO();
        dto.setId(diagnosis.getId());
        dto.setAppointmentId(diagnosis.getAppointment().getId());
        dto.setDescription(diagnosis.getDescription());
        dto.setICDCode(diagnosis.getICDCode());
        return dto;
    }

    private void updateEntityFromDTO(DiagnosisDTO dto, Diagnosis diagnosis) {
        Appointment appointment = appointmentRepository.findById(dto.getAppointmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Прийом не знайдено"));

        if (!"COMPLETED".equals(appointment.getStatus())) {
            throw new IllegalStateException("Неможливо додати діагноз: прийом ще не завершений або скасований.");
        }

        diagnosis.setAppointment(appointment);
        diagnosis.setDescription(dto.getDescription());
        diagnosis.setICDCode(dto.getICDCode());
    }

    // --- Методи сервісу ---

    public Page<DiagnosisDTO> getAllDiagnoses(Pageable pageable) {
        return diagnosisRepository.findAll(pageable).map(this::mapToDTO);
    }

    public DiagnosisDTO getDiagnosisById(Long id) {
        return diagnosisRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Diagnosis not found"));
    }

    @Transactional // Обов'язково для забезпечення цілісності
    public DiagnosisDTO createDiagnosis(DiagnosisDTO dto) {
        Diagnosis diagnosis = new Diagnosis();
        updateEntityFromDTO(dto, diagnosis);
        return mapToDTO(diagnosisRepository.save(diagnosis));
    }

    @Transactional
    public DiagnosisDTO updateDiagnosis(Long id, DiagnosisDTO dto) {
        Diagnosis diagnosis = diagnosisRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Diagnosis not found"));

        updateEntityFromDTO(dto, diagnosis);
        return mapToDTO(diagnosisRepository.save(diagnosis));
    }

    @Transactional
    public void deleteDiagnosis(Long id) {
        if (!diagnosisRepository.existsById(id)) {
            throw new ResourceNotFoundException("Diagnosis not found");
        }
        diagnosisRepository.deleteById(id);
    }
}