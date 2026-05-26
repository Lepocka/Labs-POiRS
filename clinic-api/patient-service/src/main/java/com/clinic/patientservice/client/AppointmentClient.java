package com.clinic.patientservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;
import java.util.Map;

@FeignClient(name = "appointment-client", url = "http://localhost:8082/api/appointments")
public interface AppointmentClient {
    @GetMapping("/history/{historyId}")
    List<Object> fetchAppointmentsByHistory(@PathVariable("historyId") Long historyId);
    @GetMapping("/{id}")
    Map<String, Object> getAppointmentById(@PathVariable("id") Long id);
    @GetMapping("/patient/{patientId}")
    List<Object> fetchAppointmentsByPatient(@PathVariable("patientId") Long patientId);
}
