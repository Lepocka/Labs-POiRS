package com.clinic.clinic_api.service;

import com.clinic.clinic_api.dto.MedicalHistoryDTO;
import com.clinic.clinic_api.exception.ResourceNotFoundException;
import com.clinic.clinic_api.model.Appointment;
import com.clinic.clinic_api.model.MedicalHistory;
import com.clinic.clinic_api.model.Patient;
import com.clinic.clinic_api.repository.MedicalHistoryRepository;
import com.clinic.clinic_api.repository.PatientRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
public class MedicalHistoryService {
    private final MedicalHistoryRepository medicalHistoryRepository;
    private final PatientRepository patientRepository; // Потрібен для зв'язку з пацієнтом

    public MedicalHistoryService(MedicalHistoryRepository medicalHistoryRepository,
                                 PatientRepository patientRepository) {
        this.medicalHistoryRepository = medicalHistoryRepository;
        this.patientRepository = patientRepository;
    }

    // --- Оновлений Мапінг ---

    private MedicalHistoryDTO mapToDTO(MedicalHistory medicalHistory) {
        MedicalHistoryDTO dto = new MedicalHistoryDTO();
        dto.setId(medicalHistory.getId());
        // Дістаємо ID через об'єкт зв'язку
        dto.setPatientId(medicalHistory.getPatient().getId());

        // Перетворюємо список об'єктів Appointment у список їхніх ID для DTO
        dto.setAppointmentIds(medicalHistory.getHistoryRecords().stream()
                .map(Appointment::getId)
                .collect(Collectors.toList()));

        dto.setBloodType(medicalHistory.getBloodType());
        return dto;
    }

    private void updateEntityFromDTO(MedicalHistoryDTO dto, MedicalHistory medicalHistory) {
        Patient patient = patientRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Пацієнта не знайдено"));

        medicalHistory.setPatient(patient);
        medicalHistory.setBloodType(dto.getBloodType());
        // Примітка: Список прийомів (historyRecords) зазвичай не оновлюється вручну через DTO,
        // а наповнюється автоматично системою при завершенні прийомів.
    }

    // --- Методи сервісу ---

    public Page<MedicalHistoryDTO> getAllMedicalHistories(Pageable pageable) {
        return medicalHistoryRepository.findAll(pageable).map(this::mapToDTO);
    }

    public MedicalHistoryDTO getMedicalHistoryById(Long id) {
        return medicalHistoryRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new ResourceNotFoundException("History not found"));
    }
    public MedicalHistoryDTO getByPatientId(Long patientId) {
        return medicalHistoryRepository.findByPatientId(patientId)
                .map(this::mapToDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Медичну історію для пацієнта з ID " + patientId + " не знайдено"));
    }
    @Transactional
    public MedicalHistoryDTO createMedicalHistory(MedicalHistoryDTO dto) {
        // БІЗНЕС-ЛОГІКА: Перевірка на унікальність (1 пацієнт = 1 історія)
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

        // ПОВНОЦІННА БІЗНЕС-ЛОГІКА: Заборона зміни групи крові після її встановлення
        if (history.getBloodType() != null && !history.getBloodType().equals(dto.getBloodType())) {
            throw new IllegalArgumentException("Зміна групи крові заборонена з міркувань медичної безпеки.");
        }

        updateEntityFromDTO(dto, history);
        return mapToDTO(medicalHistoryRepository.save(history));
    }

    @Transactional
    public void deleteMedicalHistory(Long id) {
        MedicalHistory history = medicalHistoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("History not found"));

        // БІЗНЕС-ЛОГІКА: Заборона видалення історії, якщо вона містить записи про візити
        if (!history.getHistoryRecords().isEmpty()) {
            throw new IllegalStateException("Неможливо видалити медичну історію, оскільки вона містить записи про прийоми.");
        }

        medicalHistoryRepository.delete(history);
    }
}