package com.clinic.patientservice.service;

import com.clinic.patientservice.dto.DiagnosisDTO;
import com.clinic.patientservice.exception.ResourceNotFoundException;
import com.clinic.patientservice.model.Diagnosis;
import com.clinic.patientservice.repository.DiagnosisRepository;
import com.clinic.patientservice.client.AppointmentClient; // Наш Feign клієнт
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
@RequiredArgsConstructor // Lombok сам створить конструктор для final полів!
public class DiagnosisService {

    private final DiagnosisRepository diagnosisRepository;
    private final AppointmentClient appointmentClient; // Замість репозиторію тепер клієнт

    // --- Мапінг ---

    private DiagnosisDTO mapToDTO(Diagnosis diagnosis) {
        DiagnosisDTO dto = new DiagnosisDTO();
        dto.setId(diagnosis.getId());
        dto.setAppointmentId(diagnosis.getAppointmentId()); // Беремо просто ID
        dto.setDescription(diagnosis.getDescription());
        dto.setICDCode(diagnosis.getICDCode());
        return dto;
    }

    private void updateEntityFromDTO(DiagnosisDTO dto, Diagnosis diagnosis) {
        // РОБИМО МЕРЕЖЕВИЙ ЗАПИТ до appointment-service
        // Використовуємо Map для простоти, щоб не плодити зайві DTO класи
        Map<String, Object> appointmentInfo = appointmentClient.getAppointmentById(dto.getAppointmentId());

        if (appointmentInfo == null) {
            throw new ResourceNotFoundException("Прийом не знайдено в базі appointment-service");
        }

        // Дістаємо статус з JSON-відповіді
        String status = (String) appointmentInfo.get("status");

        if (!"COMPLETED".equals(status)) {
            throw new IllegalStateException("Неможливо додати діагноз: прийом ще не завершений або скасований.");
        }

        diagnosis.setAppointmentId(dto.getAppointmentId()); // Зберігаємо тільки ID
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

    @Transactional
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

    // Твій відновлений метод для віддачі списку!
    public List<DiagnosisDTO> getByAppointmentId(Long appointmentId) {
        return diagnosisRepository.findAllByAppointmentId(appointmentId)
                .stream().map(this::mapToDTO).toList(); // Тепер конвертує нормально
    }
}