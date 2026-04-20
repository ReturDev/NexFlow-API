package com.returdev.nexflow.annotations.swagger;

import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom annotation to document a method that returns a successful HTTP 200 response in an OpenAPI specification using Swagger.
 * <p>
 * This annotation indicates that the API endpoint returns an HTTP 200 status code,
 * signifying that the request was processed successfully.
 * </p>
 * <strong>Example usage:</strong>
 * <pre>{@code
 * @OkResponseCode
 * public List<Item> getItems() {
 *     return itemService.getAllItems();
 * }
 * }</pre>
 * </p>
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@ApiResponse(
        responseCode = "200",
        description = "Retrieved successfully",
        useReturnTypeSchema = true
)
public @interface OkResponseCode {}