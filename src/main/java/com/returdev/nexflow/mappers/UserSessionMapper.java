package com.returdev.nexflow.mappers;

import com.returdev.nexflow.dto.response.UserSessionResponseDTO;
import com.returdev.nexflow.model.entities.UserSessionEntity;
import org.springframework.stereotype.Component;

/**
 * Component responsible for mapping {@link UserSessionEntity} objects to
 * {@link UserSessionResponseDTO} objects.
 */
@Component
public class UserSessionMapper implements ResponseMapper<UserSessionEntity, UserSessionResponseDTO> {

    /**
     * Converts a {@link UserSessionEntity} into a {@link UserSessionResponseDTO}.
     *
     * @param sessionEntity the source database entity representing an active session.
     * @return a populated {@link UserSessionResponseDTO} containing the session details.
     */
    @Override
    public UserSessionResponseDTO toResponse(UserSessionEntity sessionEntity) {
        return new UserSessionResponseDTO(
                sessionEntity.getId(),
                sessionEntity.getDeviceInfo(),
                sessionEntity.getLastActive(),
                sessionEntity.getUser().getId()
        );
    }
}
