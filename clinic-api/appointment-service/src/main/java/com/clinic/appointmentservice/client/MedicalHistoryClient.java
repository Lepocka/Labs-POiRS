package com.clinic.appointmentservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.Map;

@FeignClient(name = "medical-history-client", url = "http://localhost:8081/api/medical-histories")
public interface MedicalHistoryClient {
    @GetMapping("/patient/{patientId}")
    Map<String, Object> getByPatientId(@PathVariable("patientId") Long patientId);
}