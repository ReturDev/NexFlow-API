package com.returdev.nexflow.dto.response.wrapper;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * A generic wrapper for paginated collection responses.
 * <p>
 * Combines the requested data list with a "pageInfo" object containing
 * metadata necessary for frontend pagination controls.
 *
 * @param <T>      the type of the resources in the collection.
 * @param content  the list of resources for the current page.
 * @param pageInfo metadata regarding the pagination state (size, totals, etc.).
 */
public record PaginationWrapperResponseDTO<T>(
        @JsonProperty("data") List<T> content,
        @JsonProperty("pageInfo") PageInfo pageInfo
) {

    /**
     * Internal metadata record representing the state of a paginated result.
     *
     * @param pageSize      number of elements requested per page.
     * @param totalElements total number of elements available across all pages.
     * @param totalPages    total number of pages calculated based on size.
     * @param pageNumber    the current page index (1-indexed for client convenience).
     */
    private record PageInfo(
            @JsonProperty("size") int pageSize,
            @JsonProperty("totalElements") long totalElements,
            @JsonProperty("totalPages") int totalPages,
            @JsonProperty("pageNumber") int pageNumber
    ) {
    }

    /**
     * Maps a Spring Data {@link Page} object into a standardized pagination DTO.
     * <p>
     * Note: The page number is incremented by 1 to convert Spring's 0-based
     * indexing into a 1-based index for the API consumer.
     *
     * @param <T>  the type of the page content.
     * @param page the Spring Data {@link Page} result from a repository.
     * @return a formatted {@link PaginationWrapperResponseDTO}.
     */
    public static <T> PaginationWrapperResponseDTO<T> fromPage(Page<T> page) {
        return new PaginationWrapperResponseDTO<>(
                page.getContent(),
                new PageInfo(
                        page.getSize(),
                        page.getTotalElements(),
                        page.getTotalPages(),
                        page.getNumber() + 1
                )
        );
    }

}
