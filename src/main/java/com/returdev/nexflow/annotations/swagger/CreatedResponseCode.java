package com.returdev.nexflow.annotations.swagger;

import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom annotation to document a method that returns a 201 Created response
 * in an OpenAPI specification using Swagger.
 * <p>
 * This annotation indicates that the API endpoint successfully creates a resource,
 * resulting in a 201 status code, which signifies that the request has been fulfilled
 * and has led to the creation of a new resource.
 * </p>
 * <strong>Example usage:</strong>
 * <pre>{@code
 * @CreatedResponseCode
 * public ResponseEntity<Item> createItem(Item item) {
 *     return itemService.createItem(item);
 * }
 * }</pre>
 * </p>
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@ApiResponse(
        responseCode = "201",
        description = "Created successfully",
        useReturnTypeSchema = true
)
public @interface CreatedResponseCode {}