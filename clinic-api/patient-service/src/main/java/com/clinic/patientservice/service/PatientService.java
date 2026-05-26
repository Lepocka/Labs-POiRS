package com.clinic.patientservice.service;

import com.clinic.patientservice.client.AppointmentClient;
import com.clinic.patientservice.dto.PatientDTO;
import com.clinic.patientservice.exception.ResourceNotFoundException;
import com.clinic.patientservice.model.MedicalHistory;
import com.clinic.patientservice.model.Patient;
import com.clinic.patientservice.repository.MedicalHistoryRepository;
import com.clinic.patientservice.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor // Lombok для генерації конструктора
public class PatientService {

    private final PatientRepository patientRepository;
    private final MedicalHistoryRepository medicalHistoryRepository;
    private final AppointmentClient appointmentClient; // Мережевий клієнт для прийомів

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

        // БІЗНЕС-ЛОГІКА: Перевірка активних записів перед видаленням ЧЕРЕЗ МЕРЕЖУ
        try {
            List<Object> appointments = appointmentClient.fetchAppointmentsByPatient(id);
            if (appointments != null) {
                // Парсимо JSON-об'єкти, щоб знайти статуси
                boolean hasActiveAppointments = appointments.stream()
                        .map(obj -> (Map<?, ?>) obj)
                        .anyMatch(app -> "SCHEDULED".equals(app.get("status")));

                if (hasActiveAppointments) {
                    throw new IllegalStateException("Неможливо видалити пацієнта: є активні заплановані прийоми. Спочатку скасуйте їх.");
                }
            }
        } catch (Exception e) {
            log.error("Помилка зв'язку з appointment-service: {}", e.getMessage());
            throw new IllegalStateException("Не вдалося перевірити прийоми пацієнта через недоступність сервісу розкладу. Видалення скасовано.");
        }

        // Завдяки CascadeType.ALL у Entity, медична історія видалиться автоматично
        patientRepository.delete(patient);
    }
}