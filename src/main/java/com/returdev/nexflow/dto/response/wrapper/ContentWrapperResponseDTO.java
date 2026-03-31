package com.returdev.nexflow.dto.response.wrapper;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A generic wrapper for single-resource API responses.
 * <p>
 * Ensures all single object responses are wrapped in a consistent "data" field,
 * preventing top-level JSON array issues and allowing for future metadata expansion.
 *
 * @param <T>     the type of the resource being wrapped.
 * @param content the actual data payload.
 */
public record ContentWrapperResponseDTO<T>(
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
