package com.clinic.clinic_api.service;

import com.clinic.clinic_api.dto.TreatmentDTO;
import com.clinic.clinic_api.exception.ResourceNotFoundException;
import com.clinic.clinic_api.model.Treatment;
import com.clinic.clinic_api.repository.TreatmentRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TreatmentService {
    private final TreatmentRepository treatmentRepository;

    public TreatmentService(TreatmentRepository treatmentRepository) {
        this.treatmentRepository = treatmentRepository;
    }

    private TreatmentDTO mapToDTO(Treatment treatment) {
        TreatmentDTO dto = new TreatmentDTO();
        dto.setId(treatment.getId());
        dto.setDiagnosisId(treatment.getDiagnosisId());
        dto.setMedicineName(treatment.getMedicineName());
        dto.setDosage(treatment.getDosage());
        dto.setDurationDays(treatment.getDurationDays());
        return dto;
    }

    public List<TreatmentDTO> getAllTreatments() {
        return treatmentRepository.findAll().stream().map(this::mapToDTO).toList();
    }

    public TreatmentDTO createTreatment(TreatmentDTO dto) {
        Treatment treatment = new Treatment();
        treatment.setDiagnosisId(dto.getDiagnosisId());
        treatment.setMedicineName(dto.getMedicineName());
        treatment.setDosage(dto.getDosage());
        treatment.setDurationDays(dto.getDurationDays());

        Treatment savedTreatment = treatmentRepository.save(treatment);
        return mapToDTO(savedTreatment);
    }

    public TreatmentDTO getTreatmentById(Long id) {
        Treatment treatment = treatmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Treatment with ID " + id + " not found"));
        return mapToDTO(treatment);
    }
}