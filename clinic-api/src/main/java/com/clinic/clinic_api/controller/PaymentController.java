package com.clinic.clinic_api.controller;

import com.clinic.clinic_api.dto.PaymentDTO;
import com.clinic.clinic_api.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    private final PaymentService paymentService;
    public PaymentController(PaymentService paymentService) { this.paymentService = paymentService; }

    @GetMapping
    public List<PaymentDTO> getAll() { return paymentService.getAllPayments(); }

    @GetMapping("/{id}")
    public PaymentDTO getById(@PathVariable Long id) {
        return paymentService.getPaymentById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentDTO create(@Valid @RequestBody PaymentDTO payment) {
        return paymentService.createPayment(payment);
    }
}