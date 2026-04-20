package com.returdev.nexflow.annotations.swagger;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom annotation to document a method that returns a 400 Bad Request response
 * in an OpenAPI specification using Swagger.
 * <p>
 * This annotation indicates that the API endpoint may result in a 400 status code,
 * which signifies that the request was invalid or cannot be processed due to client error.
 * </p>
 * <strong>Example usage:</strong>
 * <pre>{@code
 * @BadRequestResponseCode
 * public ResponseEntity<Item> updateItem(Long id, Item item) {
 *     return itemService.updateItem(id, item);
 * }
 * }</pre>
 * </p>
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@ApiResponse(
        responseCode = "400",
        description = "Bad Request: The request was invalid or cannot be processed due to client error.",
        content = @Content(
                schema = @Schema(
                        ref = "#/components/schemas/BadRequestErrorResponse",
                        description = "Error response object containing details about the bad request."
                )
        )

)
public @interface BadRequestResponseCode {
}
