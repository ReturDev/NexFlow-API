package com.returdev.nexflow.annotations.swagger;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom annotation to document a method that returns a 409 Conflict response
 * in an OpenAPI specification using Swagger.
 * <p>
 * This annotation indicates that the API endpoint may result in a 409 status code,
 * which signifies that the request could not be completed due to a conflict with the current state
 * of the resource.
 * </p>
 * <strong>Example usage:</strong>
 * <pre>{@code
 * @ConflictResponseCode
 * public ResponseEntity<Item> createItem(Item item) {
 *     return itemService.createItem(item);
 * }
 * }</pre>
 * </p>
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@ApiResponse(
        responseCode = "409",
        description = "Conflict: The request could not be completed due to a conflict with the current state of the resource.",
        content = @Content(
                schema = @Schema(
                        ref = "#/components/schemas/ErrorResponse",
                        description = "Error response object containing details about the conflict."
                )
        )
)
public @interface ConflictResponseCode {}
