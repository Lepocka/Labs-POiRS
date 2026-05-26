package com.clinic.patientservice.service;

import com.clinic.patientservice.client.AppointmentClient;
import com.clinic.patientservice.dto.MedicalHistoryDTO;
import com.clinic.patientservice.exception.ResourceNotFoundException;
import com.clinic.patientservice.model.MedicalHistory;
import com.clinic.patientservice.model.Patient;
import com.clinic.patientservice.repository.MedicalHistoryRepository;
import com.clinic.patientservice.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor // Lombok сам згенерує чистий конструктор для всіх final полів
public class MedicalHistoryService {

    private final AppointmentClient appointmentClient;
    private final MedicalHistoryRepository medicalHistoryRepository;
    private final PatientRepository patientRepository;

    // --- Мапінг ---

    // Базовий мапінг (залишає список ID прийомів порожнім для безпечного відображення в реєстрах/пагінації)
    private MedicalHistoryDTO mapToDTO(MedicalHistory medicalHistory) {
        MedicalHistoryDTO dto = new MedicalHistoryDTO();
        dto.setId(medicalHistory.getId());
        dto.setPatientId(medicalHistory.getPatient().getId());
        dto.setBloodType(medicalHistory.getBloodType());
        return dto;
    }

    // Допоміжний метод: витягує JSON-об'єкти через Feign і дістає з них суто ID прийомів
    private List<Long> fetchAppointmentIds(Long historyId) {
        try {
            List<Object> appointments = appointmentClient.fetchAppointmentsByHistory(historyId);
            if (appointments == null) return Collections.emptyList();

            // Оскільки Feign повертає список LinkedHashMap, дістаємо поле "id" вручну
            return appointments.stream()
                    .map(obj -> {
                        if (obj instanceof Map) {
                            Number id = (Number) ((Map<?, ?>) obj).get("id");
                            return id != null ? id.longValue() : null;
                        }
                        return null;
                    })
                    .filter(Objects::nonNull)
                    .toList();
        } catch (Exception e) {
            // Якщо сусідній мікросервіс тимчасово лежить — повертаємо порожній список, щоб не падав весь сервіс
            return Collections.emptyList();
        }
    }

    private void updateEntityFromDTO(MedicalHistoryDTO dto, MedicalHistory medicalHistory) {
        Patient patient = patientRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Пацієнта не знайдено"));

        medicalHistory.setPatient(patient);
        medicalHistory.setBloodType(dto.getBloodType());
    }

    // --- Методи сервісу ---

    public Page<MedicalHistoryDTO> getAllMedicalHistories(Pageable pageable) {
        return medicalHistoryRepository.findAll(pageable).map(this::mapToDTO);
    }

    public MedicalHistoryDTO getMedicalHistoryById(Long id) {
        MedicalHistory history = medicalHistoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("History not found"));

        MedicalHistoryDTO dto = mapToDTO(history);
        // ВІДНОВЛЕННЯ СПИСКУ: динамічно підтягуємо ID через Feign-клієнт
        dto.setAppointmentIds(fetchAppointmentIds(id));
        return dto;
    }

    public MedicalHistoryDTO getByPatientId(Long patientId) {
        MedicalHistory history = medicalHistoryRepository.findByPatientId(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Медичну історію для пацієнта з ID " + patientId + " не знайдено"));

        MedicalHistoryDTO dto = mapToDTO(history);
        dto.setAppointmentIds(fetchAppointmentIds(history.getId()));
        return dto;
    }

    @Transactional
    public MedicalHistoryDTO createMedicalHistory(MedicalHistoryDTO dto) {
        if (medicalHistoryRepository.existsByPatientId(dto.getPatientId())) {
            throw new IllegalStateException("У цього пацієнта вже існує медична історія.");
        }

        MedicalHistory history = new MedicalHistory();
        updateEntityFromDTO(dto, history);
        return mapToDTO(medicalHistoryRepository.save(history));
    }

    @Transactional
    public MedicalHistoryDTO updateMedicalHistory(Long id, MedicalHistoryDTO dto) {
        MedicalHistory history = medicalHistoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("History not found"));

        if (history.getBloodType() != null && !history.getBloodType().equals(dto.getBloodType())) {
            throw new IllegalArgumentException("Зміна групи крові заборонена з міркувань медичної безпеки.");
        }

        updateEntityFromDTO(dto, history);
        MedicalHistoryDTO updatedDto = mapToDTO(medicalHistoryRepository.save(history));
        updatedDto.setAppointmentIds(fetchAppointmentIds(id));
        return updatedDto;
    }

    @Transactional
    public void deleteMedicalHistory(Long id) {
        MedicalHistory history = medicalHistoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("History not found"));

        // БІЗНЕС-ЛОГІКА: Перевіряємо наявність записів через мережевий запит до appointment-service
        List<Object> appointments = appointmentClient.fetchAppointmentsByHistory(id);
        if (appointments != null && !appointments.isEmpty()) {
            throw new IllegalStateException("Неможливо видалити медичну історію, оскільки вона містить записи про прийоми.");
        }

        medicalHistoryRepository.delete(history);
    }

    // Твій повноцінний метод для збору всієї інформації докупи
    public MedicalHistoryDTO getFullHistoryInfo(Long historyId) {
        MedicalHistory history = medicalHistoryRepository.findById(historyId)
                .orElseThrow(() -> new ResourceNotFoundException("History not found"));

        MedicalHistoryDTO dto = mapToDTO(history);
        dto.setAppointmentIds(fetchAppointmentIds(historyId));
        return dto;
    }
}