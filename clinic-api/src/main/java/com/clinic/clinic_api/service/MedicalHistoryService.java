package com.clinic.clinic_api.service;

import com.clinic.clinic_api.dto.MedicalHistoryDTO;
import com.clinic.clinic_api.exception.ResourceNotFoundException;
import com.clinic.clinic_api.model.MedicalHistory;
import com.clinic.clinic_api.repository.MedicalHistoryRepository;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class MedicalHistoryService {
    private final MedicalHistoryRepository medicalHistoryRepository;

    public MedicalHistoryService(MedicalHistoryRepository medicalHistoryRepository) {
        this.medicalHistoryRepository = medicalHistoryRepository;
    }

    private MedicalHistoryDTO mapToDTO(MedicalHistory medicalHistory) {
        MedicalHistoryDTO dto = new MedicalHistoryDTO();
        dto.setId(medicalHistory.getId());
        dto.setPatientId(medicalHistory.getPatientId());
        // Захист від null при копіюванні списку
        dto.setAppointmentIds(medicalHistory.getAppointmentIds() != null ? new ArrayList<>(medicalHistory.getAppointmentIds()) : new ArrayList<>());
        dto.setBloodType(medicalHistory.getBloodType());
        return dto;
    }

    public List<MedicalHistoryDTO> getAllMedicalHistories() {
        return medicalHistoryRepository.findAll().stream().map(this::mapToDTO).toList();
    }

    public MedicalHistoryDTO createMedicalHistory(MedicalHistoryDTO dto) {
        MedicalHistory medicalHistory = new MedicalHistory();
        medicalHistory.setPatientId(dto.getPatientId());
        medicalHistory.setAppointmentIds(dto.getAppointmentIds() != null ? new ArrayList<>(dto.getAppointmentIds()) : new ArrayList<>());
        medicalHistory.setBloodType(dto.getBloodType());

        MedicalHistory savedHistory = medicalHistoryRepository.save(medicalHistory);
        return mapToDTO(savedHistory);
    }

    public MedicalHistoryDTO getMedicalHistoryById(Long id) {
        MedicalHistory medicalHistory = medicalHistoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Medical History with ID " + id + " not found"));
        return mapToDTO(medicalHistory);
    }
}