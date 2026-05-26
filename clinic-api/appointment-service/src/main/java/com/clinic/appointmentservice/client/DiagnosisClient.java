package com.clinic.appointmentservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;

@FeignClient(name = "diagnosis-client", url = "http://localhost:8081/api/diagnoses")
public interface DiagnosisClient {
    @GetMapping("/appointment/{appointmentId}")
    List<Object> fetchDiagnosesByAppointment(@PathVariable("appointmentId") Long appointmentId);
}