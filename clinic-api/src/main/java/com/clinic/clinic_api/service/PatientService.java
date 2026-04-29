package com.clinic.clinic_api.service;

import com.clinic.clinic_api.dto.PatientDTO;
import com.clinic.clinic_api.exception.ResourceNotFoundException;
import com.clinic.clinic_api.model.Patient;
import com.clinic.clinic_api.repository.PatientRepository;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class PatientService {
    private final PatientRepository patientRepository;

    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    private PatientDTO mapToDTO(Patient patient) {
        PatientDTO dto = new PatientDTO();
        dto.setId(patient.getId());
        dto.setName(patient.getName());
        dto.setPhone(patient.getPhone());
        dto.setBirthDate(patient.getBirthDate());
        return dto;
    }

    public List<PatientDTO> getAllPatients() {
        List<Patient> patients = patientRepository.findAll();
        return patients.stream().map(this::mapToDTO).toList();
    }

    public PatientDTO createPatient(PatientDTO dto) {
        // 1. DTO -> Entity (Мапінг "туди")
        Patient patient = new Patient();
        patient.setName(dto.getName());
        patient.setPhone(dto.getPhone());
        patient.setBirthDate(dto.getBirthDate());

        // 2. Збереження у репозиторій
        Patient savedPatient = patientRepository.save(patient);

        // 3. Entity -> DTO (Мапінг "назад")
        return mapToDTO(savedPatient);
    }

    public PatientDTO getPatientById(Long id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient with ID " + id + " not found"));
        return mapToDTO(patient);
    }
}
