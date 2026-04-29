package com.clinic.clinic_api.repository;

import com.clinic.clinic_api.model.Payment;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class PaymentRepository {
    private final Map<Long, Payment> payments = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public List<Payment> findAll() {
        return new ArrayList<>(payments.values());
    }

    public Optional<Payment> findById(Long id) {
        return Optional.ofNullable(payments.get(id));
    }

    public Payment save(Payment Payment) {
        if (Payment.getId() == null) {
            Payment.setId(idGenerator.getAndIncrement());
        }
        payments.put(Payment.getId(), Payment);
        return Payment;
    }

    public void deleteById(Long id) {
        payments.remove(id);
    }
}