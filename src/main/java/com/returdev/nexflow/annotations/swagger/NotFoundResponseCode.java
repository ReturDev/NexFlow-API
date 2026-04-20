package com.returdev.nexflow.annotations.swagger;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom annotation to document a method that returns a 404 Not Found response
 * in an OpenAPI specification using Swagger.
 * <p>
 * This annotation indicates that the API endpoint could not find the requested resource.
 * </p>
 * <p>
 * <strong>Example usage:</strong>
 * <pre>{@code
 * @NotFoundResponseCode
 * public ResponseEntity<Item> getItem(Long id) {
 *     Item item = itemService.findById(id);
 *     if (item == null) {
 *         throw new EntityNotFoundException("Item not found with ID: " + id);
 *     }
 *     return ResponseEntity.ok(item);
 * }
 * }</pre>
 * </p>
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@ApiResponse(
        responseCode = "404",
        description = "Not Found: The requested resource could not be found.",
        content = @Content(
                schema = @Schema(
                        ref = "#/components/schemas/ErrorResponse",
                        description = "Error response object containing details about the resource that could not be found."
                )
        )
)
public @interface NotFoundResponseCode {}
