package com.returdev.nexflow.mappers;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

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
 * @param <Update>   the DTO type used for partial update requests.
 */
public interface Mapper<Entity, Response, Request, Update> {

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
     * Generic contract for performing partial updates on an existing entity.
     *
     * @param dto    the DTO containing the potential updates.
     * @param entity the existing database entity to be modified.
     */
    void updateEntity(Update dto, Entity entity);

    /**
     * Normalizes a zoned timestamp to a standardized UTC {@link LocalDateTime}.
     *
     * @param date the source {@link OffsetDateTime} (e.g., "2026-04-03T10:00-05:00").
     * @return a {@link LocalDateTime} representing that same moment in UTC.
     */
    default LocalDateTime normalizeDateToUTC(OffsetDateTime date) {
        return date.withOffsetSameInstant(ZoneOffset.UTC).toLocalDateTime();
    }

}
