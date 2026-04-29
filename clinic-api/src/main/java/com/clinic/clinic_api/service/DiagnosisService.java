package com.clinic.clinic_api.service;

import com.clinic.clinic_api.dto.DiagnosisDTO;
import com.clinic.clinic_api.exception.ResourceNotFoundException;
import com.clinic.clinic_api.model.Diagnosis;
import com.clinic.clinic_api.repository.DiagnosisRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DiagnosisService {
    private final DiagnosisRepository diagnosisRepository;

    public DiagnosisService(DiagnosisRepository diagnosisRepository) {
        this.diagnosisRepository = diagnosisRepository;
    }

    private DiagnosisDTO mapToDTO(Diagnosis diagnosis) {
        DiagnosisDTO dto = new DiagnosisDTO();
        dto.setId(diagnosis.getId());
        dto.setAppointmentId(diagnosis.getAppointmentId());
        dto.setDescription(diagnosis.getDescription());
        dto.setICDCode(diagnosis.getICDCode());
        return dto;
    }

    public List<DiagnosisDTO> getAllDiagnoses() {
        return diagnosisRepository.findAll().stream().map(this::mapToDTO).toList();
    }

    public DiagnosisDTO createDiagnosis(DiagnosisDTO dto) {
        Diagnosis diagnosis = new Diagnosis();
        diagnosis.setAppointmentId(dto.getAppointmentId());
        diagnosis.setDescription(dto.getDescription());
        diagnosis.setICDCode(dto.getICDCode());

        Diagnosis savedDiagnosis = diagnosisRepository.save(diagnosis);
        return mapToDTO(savedDiagnosis);
    }

    public DiagnosisDTO getDiagnosisById(Long id) {
        Diagnosis diagnosis = diagnosisRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Diagnosis with ID " + id + " not found"));
        return mapToDTO(diagnosis);
    }
}