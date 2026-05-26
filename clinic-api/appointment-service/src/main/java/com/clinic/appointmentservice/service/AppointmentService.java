package com.clinic.appointmentservice.service;

import com.clinic.appointmentservice.client.DiagnosisClient;
import com.clinic.appointmentservice.client.MedicalHistoryClient;
import com.clinic.appointmentservice.client.PatientClient;
import com.clinic.appointmentservice.dto.AppointmentDTO;
import com.clinic.appointmentservice.exception.ResourceNotFoundException;
import com.clinic.appointmentservice.model.Appointment;
import com.clinic.appointmentservice.model.Doctor;
import com.clinic.appointmentservice.model.enums.AppointmentStatus;
import com.clinic.appointmentservice.repository.AppointmentRepository;
import com.clinic.appointmentservice.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;

    // Мережеві клієнти замість локальних репозиторіїв
    private final PatientClient patientClient;
    private final MedicalHistoryClient medicalHistoryClient;
    private final DiagnosisClient diagnosisClient;

    // --- Мапінг ---

    private AppointmentDTO mapToDTO(Appointment appointment) {
        AppointmentDTO dto = new AppointmentDTO();
        dto.setId(appointment.getId());
        dto.setPatientId(appointment.getPatientId()); // Змінено на ID
        dto.setDoctorId(appointment.getDoctor().getId());
        dto.setDateTime(appointment.getDateTime());
        dto.setStatus(appointment.getStatus());
        dto.setReason(appointment.getReason());
        return dto;
    }

    private void updateEntityFromDTO(AppointmentDTO dto, Appointment appointment) {
        // Мережева перевірка пацієнта
        Map<String, Object> patient = patientClient.getPatientById(dto.getPatientId());
        if (patient == null) {
            throw new ResourceNotFoundException("Пацієнта не знайдено в базі patient-service");
        }

        Doctor doctor = doctorRepository.findById(dto.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Лікаря не знайдено локально"));

        // Мережева перевірка медичної картки
        Map<String, Object> medicalHistory = medicalHistoryClient.getByPatientId(dto.getPatientId());
        if (medicalHistory == null) {
            throw new IllegalStateException("У пацієнта відсутня медична картка.");
        }
        Long medicalHistoryId = ((Number) medicalHistory.get("id")).longValue();

        appointment.setPatientId(dto.getPatientId()); // Зберігаємо ID
        appointment.setDoctor(doctor);
        appointment.setMedicalHistoryId(medicalHistoryId); // Зберігаємо ID
        appointment.setDateTime(dto.getDateTime());
        appointment.setStatus(dto.getStatus() != null ? dto.getStatus() : AppointmentStatus.SCHEDULED);
        appointment.setReason(dto.getReason());
    }

    // --- Методи сервісу ---

    @Transactional
    public AppointmentDTO createAppointment(AppointmentDTO dto) {
        // Мережеві запити
        Map<String, Object> patient = patientClient.getPatientById(dto.getPatientId());
        if (patient == null) throw new ResourceNotFoundException("Пацієнта не знайдено");

        Doctor doctor = doctorRepository.findById(dto.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Лікаря не знайдено"));

        Map<String, Object> medicalHistory = medicalHistoryClient.getByPatientId(dto.getPatientId());
        if (medicalHistory == null) throw new IllegalStateException("У пацієнта відсутня медична картка.");
        Long medicalHistoryId = ((Number) medicalHistory.get("id")).longValue();

        // Валідація часу
        int hour = dto.getDateTime().getHour();
        int dayOfWeek = dto.getDateTime().getDayOfWeek().getValue();

        if (hour < 8 || hour >= 18) throw new IllegalArgumentException("Клініка працює з 08:00 до 18:00.");
        if (dayOfWeek == 6 || dayOfWeek == 7) throw new IllegalArgumentException("Клініка не працює у вихідні дні.");

        int minute = dto.getDateTime().getMinute();
        if (minute != 0 && minute != 30) {
            throw new IllegalArgumentException("Запис можливий лише на початок або середину години.");
        }
        if (dto.getDateTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Неможливо створити запис на прийом у минулому часі.");
        }
        if (appointmentRepository.existsByDoctorIdAndDateTime(doctor.getId(), dto.getDateTime())) {
            throw new IllegalArgumentException("Лікар " + doctor.getName() + " вже має прийом на вказаний час.");
        }

        // Логіка педіатра (Парсимо дату народження з JSON)
        if ("Педіатр".equalsIgnoreCase(doctor.getSpecialization())) {
            String birthDateStr = (String) patient.get("birthDate");
            if (birthDateStr != null) {
                LocalDate birthDate = LocalDate.parse(birthDateStr);
                int age = Period.between(birthDate, LocalDate.now()).getYears();
                if (age >= 18) {
                    throw new IllegalArgumentException("Педіатр приймає лише дітей. Вік пацієнта: " + age);
                }
            }
        }

        Appointment appointment = new Appointment();
        appointment.setPatientId(dto.getPatientId());
        appointment.setDoctor(doctor);
        appointment.setMedicalHistoryId(medicalHistoryId);
        appointment.setDateTime(dto.getDateTime());
        appointment.setReason(dto.getReason());
        appointment.setStatus(AppointmentStatus.SCHEDULED);

        return mapToDTO(appointmentRepository.save(appointment));
    }

    @Transactional
    public AppointmentDTO completeAppointment(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Запис не знайдено"));

        if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw new IllegalArgumentException("Неможливо завершити прийом, який був скасований.");
        }
        if (appointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new IllegalArgumentException("Цей прийом вже був успішно завершений раніше.");
        }

        appointment.setStatus(AppointmentStatus.COMPLETED);
        return mapToDTO(appointmentRepository.save(appointment));
    }

    @Transactional
    public AppointmentDTO cancelAppointment(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Запис не знайдено"));

        if (appointment.getDateTime().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new IllegalStateException("Скасувати прийом можна не пізніше ніж за 2 години до початку.");
        }

        appointment.setStatus(AppointmentStatus.CANCELLED);
        return mapToDTO(appointmentRepository.save(appointment));
    }

    public Page<AppointmentDTO> getAllAppointments(Pageable pageable) {
        return appointmentRepository.findAll(pageable).map(this::mapToDTO);
    }

    public AppointmentDTO getAppointmentById(Long id) {
        return appointmentRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment with ID " + id + " not found"));
    }

    @Transactional
    public AppointmentDTO updateAppointment(Long id, AppointmentDTO dto) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));
        updateEntityFromDTO(dto, appointment);
        return mapToDTO(appointmentRepository.save(appointment));
    }

    @Transactional
    public void deleteAppointment(Long id) {
        if (!appointmentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Appointment not found");
        }
        appointmentRepository.deleteById(id);
    }

    public List<AppointmentDTO> getAppointmentsByHistoryId(Long historyId) {
        return appointmentRepository.findAllByMedicalHistoryId(historyId)
                .stream().map(this::mapToDTO).toList();
    }

    // ВІДНОВЛЕННЯ СПИСКУ ДІАГНОЗІВ
    public AppointmentDTO getFullAppointmentInfo(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));

        AppointmentDTO dto = mapToDTO(appointment);

        // Робимо мережевий запит за списком діагнозів
        try {
            List<Object> diagnoses = diagnosisClient.fetchDiagnosesByAppointment(id);
            // Тут потрібно додати поле List<Object> diagnoses (або List<Long> diagnosisIds) у твій AppointmentDTO
            // dto.setDiagnoses(diagnoses);
        } catch (Exception e) {
            log.warn("Не вдалося завантажити діагнози для прийому {}", id);
        }

        return dto;
    }

    public List<AppointmentDTO> getByPatientId(Long patientId) {
        return appointmentRepository.findAllByPatientId(patientId)
                .stream().map(this::mapToDTO).toList();
    }
}