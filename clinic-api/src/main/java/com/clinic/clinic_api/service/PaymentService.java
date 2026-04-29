package com.clinic.clinic_api.service;

import com.clinic.clinic_api.dto.PaymentDTO;
import com.clinic.clinic_api.exception.ResourceNotFoundException;
import com.clinic.clinic_api.model.Payment;
import com.clinic.clinic_api.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    private PaymentDTO mapToDTO(Payment payment) {
        PaymentDTO dto = new PaymentDTO();
        dto.setId(payment.getId());
        dto.setAppointmentId(payment.getAppointmentId());
        dto.setAmount(payment.getAmount());
        dto.setStatus(payment.getStatus());
        return dto;
    }

    public List<PaymentDTO> getAllPayments() {
        return paymentRepository.findAll().stream().map(this::mapToDTO).toList();
    }

    public PaymentDTO createPayment(PaymentDTO dto) {
        Payment payment = new Payment();
        payment.setAppointmentId(dto.getAppointmentId());
        payment.setAmount(dto.getAmount());
        payment.setStatus(dto.getStatus() != null ? dto.getStatus() : "PENDING"); // Дефолтний статус

        Payment savedPayment = paymentRepository.save(payment);
        return mapToDTO(savedPayment);
    }

    public PaymentDTO getPaymentById(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment with ID " + id + " not found"));
        return mapToDTO(payment);
    }
}