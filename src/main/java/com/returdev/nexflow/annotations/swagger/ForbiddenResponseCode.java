package com.returdev.nexflow.annotations.swagger;


import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom annotation to document a method that returns a 403 Forbidden response
 * in an OpenAPI specification using Swagger.
 * <p>
 * This annotation indicates that the API endpoint may result in a 403 status code,
 * which signifies that the server understands the request but refuses to authorize it.
 * </p>
 * <strong>Example usage:</strong>
 * <pre>{@code
 * @ForbiddenResponseCode
 * public ResponseEntity<Item> updateItem(Long id, Item item) {
 *     return itemService.updateItem(id, item);
 * }
 * }</pre>
 * </p>
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@ApiResponse(
        responseCode = "403",
        description = "Forbidden: The server understands the request but refuses to authorize it.",
        content = @Content(
                schema = @Schema(
                        ref = "#/components/schemas/ErrorResponse",
                        description = "Error response object containing details about the forbidden request."
                )
        )
)
public @interface ForbiddenResponseCode {
}
