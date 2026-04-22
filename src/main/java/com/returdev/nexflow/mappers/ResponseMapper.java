package com.returdev.nexflow.mappers;

/**
 * A generic mapper interface for converting database persistence entities
 * into API-friendly response Data Transfer Objects (DTOs).
 *
 * @param <Entity>   the type of the source database entity.
 * @param <Response> the type of the target response DTO.
 */
public interface ResponseMapper<Entity, Response> {

    /**
     * Converts a persistence entity into a response DTO.
     *
     * @param entity the source entity from the database.
     * @return a populated {@code Response} DTO, or {@code null} if the input is null.
     */
    Response toResponse(Entity entity);

}
