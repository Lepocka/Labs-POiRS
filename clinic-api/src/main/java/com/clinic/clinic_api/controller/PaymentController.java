package com.clinic.clinic_api.controller;

import com.clinic.clinic_api.dto.DiagnosisDTO;
import com.clinic.clinic_api.dto.PaymentDTO;
import com.clinic.clinic_api.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    private final PaymentService paymentService;
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping
    public Page<PaymentDTO> getAll(@PageableDefault(size = 10, sort = "id") Pageable pageable) {
        return paymentService.getAllPayments(pageable);
    }

    @GetMapping("/{id}")
    public PaymentDTO getById(@PathVariable Long id) {
        return paymentService.getPaymentById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentDTO create(@Valid @RequestBody PaymentDTO paymentDTO) {
        return paymentService.createPayment(paymentDTO);
    }

    @PutMapping("/{id}")
    public PaymentDTO update(@PathVariable Long id, @Valid @RequestBody PaymentDTO paymentDTO) {
        return paymentService.updatePayment(id, paymentDTO);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT) // Повертає 204 статус (успішно, без тіла відповіді)
    public void delete(@PathVariable Long id) {
        paymentService.deletePayment(id);
    }
}