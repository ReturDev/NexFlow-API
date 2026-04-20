package com.returdev.nexflow.config.swagger;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.MapSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ProblemDetail;

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
                .components(
                        new Components()
                                .addSchemas("BadRequestErrorResponse", createBadRequestProblemDetailSchema())
                                .addSchemas("ErrorResponse", createProblemDetailSchema())
                )
                .servers(
                        List.of(
                                new Server().url(serverPath)
                        )
                );
    }

    /**
     * Creates a basic schema for ProblemDetail.
     *
     * @return schema with standard error properties.
     */
    private Schema<ProblemDetail> createProblemDetailSchema() {
        Schema<ProblemDetail> schema = new Schema<>();
        schema.addProperty("type", new StringSchema());
        schema.addProperty("title", new StringSchema());
        schema.addProperty("status", new IntegerSchema());
        schema.addProperty("detail", new StringSchema());
        schema.addProperty("instance", new StringSchema());
        return schema;
    }

    /**
     * Creates a schema for a bad request error with additional error messages.
     *
     * @return schema for bad request errors including an "errors" property.
     */
    private Schema<ProblemDetail> createBadRequestProblemDetailSchema() {
        Schema<ProblemDetail> schema = createProblemDetailSchema();
        schema.addProperty("errors", new MapSchema()
                .addProperty("errorMsg1", new StringSchema())
                .addProperty("errorMsg2", new StringSchema())
                .addProperty("errorMsg3", new StringSchema())
        );
        return schema;
    }


}
