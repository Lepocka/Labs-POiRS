package com.clinic.appointmentservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.Map;

@FeignClient(name = "patient-client", url = "http://localhost:8081/api/patients")
public interface PatientClient {
    @GetMapping("/{id}")
    Map<String, Object> getPatientById(@PathVariable("id") Long id);
}