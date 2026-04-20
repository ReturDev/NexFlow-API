package com.returdev.nexflow.config.swagger;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@OpenAPIDefinition(
        info = @Info(
                title = "Nexflow API",
                description = "Nexflow API is a comprehensive financial management RESTful " +
                        "service designed to streamline personal wealth tracking, wallet management," +
                        " and recurring transaction planning. It provides secure endpoints to manage multiple currencies," +
                        " real-time balances, and automated financial schedules with built-in JWT authorization.",
                contact = @Contact(
                        name = "ReturDev",
                        url = "https://github.com/ReturDev",
                        email = "retur.apps.dev@gmail.com"
                ),
                version = "1.0.0-SNAPSHOT"
        ),
        security = @SecurityRequirement(
                name = "Bearer Authentication"
        )
)
@SecurityScheme(
        type = SecuritySchemeType.HTTP,
        name = "Bearer Authentication",
        bearerFormat = "JWT",
        scheme = "bearer",
        description = "Enter your JWT token in the format: Bearer <token>"
)
@Configuration
public class SwaggerConfig {

    @Value("${swagger.server.path}")
    private String serverPath;


    @Bean
    public OpenAPI customOpenApi() {

        return new OpenAPI()
                .servers(
                        List.of(
                                new Server().url(serverPath)
                        )
                );
    }


}
