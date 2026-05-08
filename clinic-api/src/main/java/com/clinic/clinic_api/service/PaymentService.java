package com.clinic.clinic_api.service;

import com.clinic.clinic_api.dto.PaymentDTO;
import com.clinic.clinic_api.exception.ResourceNotFoundException;
import com.clinic.clinic_api.model.Appointment;
import com.clinic.clinic_api.model.Payment;
import com.clinic.clinic_api.model.enums.AppointmentStatus;
import com.clinic.clinic_api.model.enums.PaymentStatus;
import com.clinic.clinic_api.repository.AppointmentRepository;
import com.clinic.clinic_api.repository.PaymentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final AppointmentRepository appointmentRepository;

    public PaymentService(PaymentRepository paymentRepository,
                          AppointmentRepository appointmentRepository) {
        this.paymentRepository = paymentRepository;
        this.appointmentRepository = appointmentRepository;
    }

    // --- Мапінг ---

    private PaymentDTO mapToDTO(Payment payment) {
        PaymentDTO dto = new PaymentDTO();
        dto.setId(payment.getId());
        dto.setAppointmentId(payment.getAppointment().getId());
        dto.setAmount(payment.getAmount());
        dto.setStatus(payment.getStatus());
        return dto;
    }

    private void updateEntityFromDTO(PaymentDTO dto, Payment payment) {
        Appointment appointment = appointmentRepository.findById(dto.getAppointmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Прийом не знайдено"));

        // ПОВНОЦІННА БІЗНЕС-ЛОГІКА: Перевірка статусу прийому
        // Не можна створювати оплату для скасованого прийому
        if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw new IllegalStateException("Неможливо виставити рахунок для скасованого прийому.");
        }

        payment.setAppointment(appointment); // Встановлюємо об'єкт
        payment.setAmount(dto.getAmount());
        payment.setStatus(dto.getStatus() != null ? dto.getStatus() : PaymentStatus.PENDING);
    }

    // --- Методи сервісу ---

    public Page<PaymentDTO> getAllPayments(Pageable pageable) {
        return paymentRepository.findAll(pageable).map(this::mapToDTO);
    }

    public PaymentDTO getPaymentById(Long id) {
        return paymentRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));
    }

    @Transactional
    public PaymentDTO createPayment(PaymentDTO dto) {
        // ПОВНОЦІННА БІЗНЕС-ЛОГІКА: Перевірка на дублікат оплати
        // Один прийом — один рахунок
        if (paymentRepository.existsByAppointmentId(dto.getAppointmentId())) {
            throw new IllegalStateException("Рахунок для цього прийому вже виставлений.");
        }

        Payment payment = new Payment();
        updateEntityFromDTO(dto, payment);
        return mapToDTO(paymentRepository.save(payment));
    }

    @Transactional
    public PaymentDTO updatePayment(Long id, PaymentDTO dto) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));

        // БІЗНЕС-ЛОГІКА: Захист проведеної оплати
        // Якщо статус уже PAID, забороняємо змінювати суму
        if (payment.getStatus() == PaymentStatus.PAID && !payment.getAmount().equals(dto.getAmount())) {
            throw new IllegalStateException("Неможливо змінити суму вже проведеної оплати.");
        }

        updateEntityFromDTO(dto, payment);
        return mapToDTO(paymentRepository.save(payment));
    }

    @Transactional
    public void deletePayment(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));

        // БІЗНЕС-ЛОГІКА: Заборона видалення підтверджених оплат
        if (payment.getStatus() == PaymentStatus.PAID) {
            throw new IllegalStateException("Неможливо видалити вже оплачений рахунок з фінансової історії.");
        }

        paymentRepository.delete(payment);
    }
}