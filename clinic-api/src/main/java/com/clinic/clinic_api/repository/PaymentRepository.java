package com.clinic.clinic_api.repository;

import com.clinic.clinic_api.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    boolean existsByAppointmentId(Long appointmentId);
}