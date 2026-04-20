package com.returdev.nexflow.dto.response.wrapper;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * A generic wrapper for single-resource API responses.
 * <p>
 * Ensures all single object responses are wrapped in a consistent "data" field,
 * preventing top-level JSON array issues and allowing for future metadata expansion.
 *
 * @param <T>     the type of the resource being wrapped.
 * @param content the actual data payload.
 */
@Schema(description = "A generic wrapper used to provide a consistent structure for single-object responses.")
public record ContentWrapperResponseDTO<T>(
        @Schema(description = "The main content of the response.")
        @JsonProperty("data") T content
) {

    /**
     * Static factory method to create a new content wrapper.
     *
     * @param <T>     the type of the content.
     * @param content the data to wrap.
     * @return a new {@link ContentWrapperResponseDTO} instance.
     */
    public static <T> ContentWrapperResponseDTO<T> of(T content) {
        return new ContentWrapperResponseDTO<>(content);
    }

}
