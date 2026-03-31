package com.returdev.nexflow.mappers;

import com.returdev.nexflow.dto.response.wrapper.ContentWrapperResponseDTO;
import com.returdev.nexflow.dto.response.wrapper.PaginationWrapperResponseDTO;
import org.springframework.data.domain.Page;

/**
 * Generic contract for mapping between Entities and Data Transfer Objects (DTOs).
 * <p>
 * This interface standardizes how the application converts incoming requests
 * into persistence entities and how entities are transformed into outgoing responses,
 * including support for wrapped and paginated results.
 *
 * @param <Entity>   the JPA entity type.
 * @param <Response> the DTO type used for API responses.
 * @param <Request>  the DTO type used for API requests.
 */
public interface Mapper<Entity,Response,Request> {

    /**
     * Converts a request DTO into a persistence entity.
     *
     * @param request the incoming request data.
     * @return a new or populated {@code Entity} instance.
     */
    Entity toEntity(Request request);

    /**
     * Converts a persistence entity into a response DTO.
     *
     * @param entity the source entity from the database.
     * @return a populated {@code Response} DTO.
     */
    Response toResponse(Entity entity);

    /**
     * Transforms a Spring Data {@link Page} of entities into a paginated DTO response.
     * <p>
     *
     * @param page the paginated result from a repository.
     * @return a {@link PaginationWrapperResponseDTO} containing the mapped items.
     */
    default PaginationWrapperResponseDTO<Response> toPaginationResponse(Page<Entity> page) {
        return PaginationWrapperResponseDTO.fromPage(
                page.map(this::toResponse)
        );
    }

    /**
     * Wraps a single entity transformation into a standard content response.
     *
     * @param entity the source entity.
     * @return a {@link ContentWrapperResponseDTO} containing the mapped response.
     */
    default ContentWrapperResponseDTO<Response> toContentResponse(Entity entity) {
        return ContentWrapperResponseDTO.of(
                toResponse(entity)
        );
    }

}
