package com.clinic.clinic_api.service;

import com.clinic.clinic_api.dto.AppointmentDTO;
import com.clinic.clinic_api.exception.ResourceNotFoundException;
import com.clinic.clinic_api.model.Appointment;
import com.clinic.clinic_api.model.Doctor;
import com.clinic.clinic_api.model.MedicalHistory;
import com.clinic.clinic_api.model.Patient;
import com.clinic.clinic_api.model.enums.AppointmentStatus;
import com.clinic.clinic_api.repository.AppointmentRepository;
import com.clinic.clinic_api.repository.DoctorRepository;
import com.clinic.clinic_api.repository.MedicalHistoryRepository;
import com.clinic.clinic_api.repository.PatientRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;

@Service
@Slf4j
public class AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final MedicalHistoryRepository medicalHistoryRepository;

    public AppointmentService(AppointmentRepository appointmentRepository,
                              PatientRepository patientRepository,
                              DoctorRepository doctorRepository,
                              MedicalHistoryRepository medicalHistoryRepository) {
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.medicalHistoryRepository = medicalHistoryRepository;
    }

    // --- Мапінг ---

    private AppointmentDTO mapToDTO(Appointment appointment) {
        AppointmentDTO dto = new AppointmentDTO();
        dto.setId(appointment.getId());
        dto.setPatientId(appointment.getPatient().getId());
        dto.setDoctorId(appointment.getDoctor().getId());
        dto.setDateTime(appointment.getDateTime());
        dto.setStatus(appointment.getStatus());
        dto.setReason(appointment.getReason());
        return dto;
    }

    private void updateEntityFromDTO(AppointmentDTO dto, Appointment appointment) {
        Patient patient = patientRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Пацієнта не знайдено"));
        Doctor doctor = doctorRepository.findById(dto.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Лікаря не знайдено"));
        MedicalHistory medicalHistory = medicalHistoryRepository.findByPatientId(patient.getId())
                .orElseThrow(() -> new IllegalStateException("У пацієнта відсутня медична картка."));

        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setMedicalHistory(medicalHistory);
        appointment.setDateTime(dto.getDateTime());
        appointment.setStatus(dto.getStatus() != null ? dto.getStatus() : AppointmentStatus.SCHEDULED);
        appointment.setReason(dto.getReason());
    }

    // --- Методи сервісу ---

    @Transactional
    public AppointmentDTO createAppointment(AppointmentDTO dto) {
        Patient patient = patientRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Пацієнта не знайдено"));
        Doctor doctor = doctorRepository.findById(dto.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Лікаря не знайдено"));
        MedicalHistory medicalHistory = medicalHistoryRepository.findByPatientId(patient.getId())
                .orElseThrow(() -> new IllegalStateException("У пацієнта відсутня медична картка."));

        int hour = dto.getDateTime().getHour();
        int dayOfWeek = dto.getDateTime().getDayOfWeek().getValue();

        if (hour < 8 || hour >= 18) {
            throw new IllegalArgumentException("Клініка працює з 08:00 до 18:00.");
        }
        if (dayOfWeek == 6 || dayOfWeek == 7) {
            throw new IllegalArgumentException("Клініка не працює у вихідні дні.");
        }

        int minute = dto.getDateTime().getMinute();
        if (minute != 0 && minute != 30) {
            throw new IllegalArgumentException("Запис можливий лише на початок або середину години (наприклад, 14:00 або 14:30).");
        }

        // Валідація бізнес-логіки
        if (dto.getDateTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Неможливо створити запис на прийом у минулому часі.");
        }

        if (appointmentRepository.existsByDoctorIdAndDateTime(doctor.getId(), dto.getDateTime())) {
            throw new IllegalArgumentException("Лікар " + doctor.getName() + " вже має прийом на вказаний час.");
        }

        if ("Педіатр".equalsIgnoreCase(doctor.getSpecialization())) {
            int age = Period.between(patient.getBirthDate(), LocalDate.now()).getYears();
            if (age >= 18) {
                throw new IllegalArgumentException("Педіатр приймає лише дітей. Вік пацієнта: " + age);
            }
        }

        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setMedicalHistory(medicalHistory);
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

        // Перевіряємо, скільки часу залишилося до прийому
        LocalDateTime now = LocalDateTime.now();
        if (appointment.getDateTime().isBefore(now.plusHours(2))) {
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

        updateEntityFromDTO(dto, appointment); // Викликаємо оновлений мапінг

        return mapToDTO(appointmentRepository.save(appointment));
    }

    @Transactional
    public void deleteAppointment(Long id) {
        if (!appointmentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Appointment not found");
        }
        appointmentRepository.deleteById(id);
    }
}