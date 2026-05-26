package com.clinic.appointmentservice.repository;

import com.clinic.appointmentservice.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    boolean existsByAppointmentId(Long appointmentId);
}