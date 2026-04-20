package com.returdev.nexflow.annotations.swagger;

import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom annotation to document a method that returns a 204 No Content response
 * in an OpenAPI specification using Swagger.
 * <p>
 * This annotation indicates that the API endpoint has successfully processed the request
 * but does not have any content to return in the response body.
 * </p>
 * <strong>Example usage:</strong>
 * <pre>{@code
 * @NoContentResponseCode
 * public ResponseEntity<Void> deleteItem(Long id) {
 *     itemService.deleteItem(id);
 *     return ResponseEntity.noContent().build();
 * }
 * }</pre>
 * </p>
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@ApiResponse(
        responseCode = "204",
        description = "No Content: The request was successful but there is no content to return."
)
public @interface NoContentResponseCode {}
