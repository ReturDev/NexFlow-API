package com.returdev.nexflow.annotations.swagger;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom annotation to document a method that returns a 500 Internal Server Error response
 * in an OpenAPI specification using Swagger.
 * <p>
 * This annotation indicates that the API endpoint may result in a 500 status code,
 * which signifies that an unexpected condition was encountered on the server.
 * </p>
 * <strong>Example usage:</strong>
 * <pre>{@code
 * @InternalServerErrorResponseCode
 * public ResponseEntity<Item> getItem(Long id) {
 *     return itemService.findItemById(id);
 * }
 * }</pre>
 * </p>
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@ApiResponse(
        responseCode = "500",
        description = "Internal Server Error: An unexpected condition was encountered.",
        content = @Content(
                schema = @Schema(
                        ref = "#/components/schemas/ErrorResponse",
                        description = "Error response object containing details of the internal server error."
                )
        )
)
public @interface InternalServerErrorResponseCode {}
