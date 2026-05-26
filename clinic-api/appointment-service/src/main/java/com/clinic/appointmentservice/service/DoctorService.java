package com.clinic.appointmentservice.service;

import com.clinic.appointmentservice.dto.DoctorDTO;
import com.clinic.appointmentservice.exception.ResourceNotFoundException;
import com.clinic.appointmentservice.model.Doctor;
import com.clinic.appointmentservice.model.enums.AppointmentStatus;
import com.clinic.appointmentservice.repository.DoctorRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class DoctorService {
    private final DoctorRepository doctorRepository;

    public DoctorService(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    // --- Мапінг ---

    private DoctorDTO mapToDTO(Doctor doctor) {
        DoctorDTO dto = new DoctorDTO();
        dto.setId(doctor.getId());
        dto.setName(doctor.getName());
        dto.setSpecialization(doctor.getSpecialization());
        return dto;
    }

    private void updateEntityFromDTO(DoctorDTO dto, Doctor doctor) {
        doctor.setName(dto.getName());
        doctor.setSpecialization(dto.getSpecialization());
    }

    // --- Методи сервісу ---

    public Page<DoctorDTO> getAllDoctors(Pageable pageable) {
        // Використання Spring Data JPA для пагінації та сортування [cite: 14, 16]
        return doctorRepository.findAll(pageable).map(this::mapToDTO);
    }

    public DoctorDTO getDoctorById(Long id) {
        return doctorRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor with ID " + id + " not found"));
    }

    @Transactional
    public DoctorDTO createDoctor(DoctorDTO dto) {
        Doctor doctor = new Doctor();
        updateEntityFromDTO(dto, doctor);
        return mapToDTO(doctorRepository.save(doctor));
    }

    @Transactional
    public DoctorDTO updateDoctor(Long id, DoctorDTO dto) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));

        updateEntityFromDTO(dto, doctor);
        return mapToDTO(doctorRepository.save(doctor));
    }

    @Transactional
    public void deleteDoctor(Long id) {
        // Отримуємо об'єкт лікаря, щоб перевірити його зв'язки
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));

        // БІЗНЕС-ЛОГІКА: Перевірка цілісності даних перед видаленням
        // Змінено тільки порівняння на Enum AppointmentStatus
        boolean hasActiveAppointments = doctor.getAppointments().stream()
                .anyMatch(app -> app.getStatus() == AppointmentStatus.SCHEDULED);

        if (hasActiveAppointments) {
            throw new IllegalStateException("Неможливо видалити лікаря: у нього є заплановані прийоми. " +
                    "Спочатку скасуйте або перенесіть записи.");
        }

        doctorRepository.delete(doctor);
    }
}