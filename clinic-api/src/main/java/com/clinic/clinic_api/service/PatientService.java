package com.clinic.clinic_api.service;

import com.clinic.clinic_api.dto.PatientDTO;
import com.clinic.clinic_api.exception.ResourceNotFoundException;
import com.clinic.clinic_api.model.MedicalHistory;
import com.clinic.clinic_api.model.Patient;
import com.clinic.clinic_api.repository.MedicalHistoryRepository;
import com.clinic.clinic_api.repository.PatientRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class PatientService {
    private final PatientRepository patientRepository;
    private final MedicalHistoryRepository medicalHistoryRepository; // Додаємо для бізнес-логіки

    public PatientService(PatientRepository patientRepository,
                          MedicalHistoryRepository medicalHistoryRepository) {
        this.patientRepository = patientRepository;
        this.medicalHistoryRepository = medicalHistoryRepository;
    }

    // --- Мапінг ---

    private PatientDTO mapToDTO(Patient patient) {
        PatientDTO dto = new PatientDTO();
        dto.setId(patient.getId());
        dto.setName(patient.getName());
        dto.setPhone(patient.getPhone());
        dto.setBirthDate(patient.getBirthDate());
        return dto;
    }

    private void mapToEntity(PatientDTO dto, Patient patient) {
        patient.setName(dto.getName());
        patient.setPhone(dto.getPhone());
        patient.setBirthDate(dto.getBirthDate());
    }

    // --- Методи сервісу ---

    public Page<PatientDTO> getAllPatients(Pageable pageable) {
        return patientRepository.findAll(pageable).map(this::mapToDTO);
    }

    public PatientDTO getPatientById(Long id) {
        return patientRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));
    }

    @Transactional
    public PatientDTO createPatient(PatientDTO dto) {
        // БІЗНЕС-ЛОГІКА: Перевірка унікальності номера телефону
        if (patientRepository.existsByPhone(dto.getPhone())) {
            throw new IllegalArgumentException("Пацієнт з номером " + dto.getPhone() + " вже зареєстрований.");
        }

        Patient patient = new Patient();
        mapToEntity(dto, patient);
        Patient savedPatient = patientRepository.save(patient);

        // ПОВНОЦІННА БІЗНЕС-ЛОГІКА: Автоматичне створення медичної історії
        // Тепер пацієнт не може існувати без своєї карти
        MedicalHistory history = new MedicalHistory();
        history.setPatient(savedPatient);
        history.setBloodType("НЕ ВИЗНАЧЕНО");
        medicalHistoryRepository.save(history);

        return mapToDTO(savedPatient);
    }

    @Transactional
    public PatientDTO updatePatient(Long id, PatientDTO dto) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));

        // Перевірка, якщо телефон змінюється на вже існуючий у іншого пацієнта
        if (!patient.getPhone().equals(dto.getPhone()) && patientRepository.existsByPhone(dto.getPhone())) {
            throw new IllegalArgumentException("Цей номер телефону вже використовується іншим пацієнтом.");
        }

        mapToEntity(dto, patient);
        return mapToDTO(patientRepository.save(patient));
    }

    @Transactional
    public void deletePatient(Long id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Пацієнта не знайдено"));

        // БІЗНЕС-ЛОГІКА: Перевірка активних записів перед видаленням
        // Використовуємо getAppointments(), бо ми налаштували зв'язок @OneToMany
        boolean hasActiveAppointments = patient.getAppointments().stream()
                .anyMatch(app -> "SCHEDULED".equals(app.getStatus()));

        if (hasActiveAppointments) {
            throw new IllegalStateException("Неможливо видалити пацієнта: є активні заплановані прийоми. Спочатку скасуйте їх.");
        }

        // Завдяки CascadeType.ALL у Entity, медична історія видалиться автоматично
        patientRepository.delete(patient);
    }
}