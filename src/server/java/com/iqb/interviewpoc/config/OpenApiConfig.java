package com.iqb.interviewpoc.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "IQB Interview POC API",
        version = "1.0.0",
        description = "REST API for managing students, courses, and exam results"
    ),
    servers = {
        @Server(url = "http://localhost:8080", description = "Local Development")
    }
)
public class OpenApiConfig {
}
