package com.clinic.clinic_api.service;

import com.clinic.clinic_api.dto.TreatmentDTO;
import com.clinic.clinic_api.exception.ResourceNotFoundException;
import com.clinic.clinic_api.model.Diagnosis;
import com.clinic.clinic_api.model.Treatment;
import com.clinic.clinic_api.repository.DiagnosisRepository;
import com.clinic.clinic_api.repository.TreatmentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TreatmentService {
    private final TreatmentRepository treatmentRepository;
    private final DiagnosisRepository diagnosisRepository; // Додаємо репозиторій діагнозів

    public TreatmentService(TreatmentRepository treatmentRepository,
                            DiagnosisRepository diagnosisRepository) {
        this.treatmentRepository = treatmentRepository;
        this.diagnosisRepository = diagnosisRepository;
    }

    // --- Оновлений Мапінг ---

    private TreatmentDTO mapToDTO(Treatment treatment) {
        TreatmentDTO dto = new TreatmentDTO();
        dto.setId(treatment.getId());
        dto.setDiagnosisId(treatment.getDiagnosis().getId());
        dto.setMedicineName(treatment.getMedicineName());
        dto.setDosage(treatment.getDosage());
        dto.setDurationDays(treatment.getDurationDays());
        return dto;
    }

    private void updateEntityFromDTO(TreatmentDTO dto, Treatment treatment) {
        Diagnosis diagnosis = diagnosisRepository.findById(dto.getDiagnosisId())
                .orElseThrow(() -> new ResourceNotFoundException("Діагноз не знайдено"));

        // БІЗНЕС-ЛОГІКА: Перевірка тривалості лікування
        if (dto.getDurationDays() <= 0) {
            throw new IllegalArgumentException("Тривалість лікування повинна бути більше 0 днів.");
        }
        if (dto.getDurationDays() > 180) {
            throw new IllegalArgumentException("Курс лікування не може перевищувати 180 днів. " +
                    "Для тривалого лікування потрібен перегляд діагнозу.");
        }

        treatment.setDiagnosis(diagnosis); // Встановлюємо об'єкт
        treatment.setMedicineName(dto.getMedicineName());
        treatment.setDosage(dto.getDosage());
        treatment.setDurationDays(dto.getDurationDays());
    }

    // --- Методи сервісу ---

    public Page<TreatmentDTO> getAllTreatments(Pageable pageable) {
        return treatmentRepository.findAll(pageable).map(this::mapToDTO);
    }

    public TreatmentDTO getTreatmentById(Long id) {
        return treatmentRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Treatment not found"));
    }

    @Transactional
    public TreatmentDTO createTreatment(TreatmentDTO dto) {
        Treatment treatment = new Treatment();
        updateEntityFromDTO(dto, treatment);
        return mapToDTO(treatmentRepository.save(treatment));
    }

    @Transactional
    public TreatmentDTO updateTreatment(Long id, TreatmentDTO dto) {
        Treatment treatment = treatmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Treatment not found"));

        updateEntityFromDTO(dto, treatment);
        return mapToDTO(treatmentRepository.save(treatment));
    }

    @Transactional
    public void deleteTreatment(Long id) {
        if (!treatmentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Treatment not found");
        }
        treatmentRepository.deleteById(id);
    }
}