package com.clinic.appointmentservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI clinicOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Clinic API - Medical System")
                        .description("REST API для управління приватною клінікою\n\n"+
                                "**Розробник:** Баранник Микола\n\n" +
                                "**Email:** mykola.barannyk.oi.2023@lpnu.ua")
                        .version("v1.0")
                        );
    }
}
