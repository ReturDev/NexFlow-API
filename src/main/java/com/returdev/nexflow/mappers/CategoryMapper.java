package com.returdev.nexflow.mappers;

import com.returdev.nexflow.dto.request.CategoryRequestDTO;
import com.returdev.nexflow.dto.response.CategoryResponseDTO;
import com.returdev.nexflow.model.entities.CategoryEntity;
import org.springframework.stereotype.Component;

/**
 * Concrete implementation of {@link Mapper} for {@link CategoryEntity}.
 */
@Component
public class CategoryMapper implements Mapper<CategoryEntity, CategoryResponseDTO, CategoryRequestDTO> {

    /**
     * Maps a {@link CategoryRequestDTO} to a {@link CategoryEntity} using the Builder pattern.
     *
     * @param request the DTO containing the category name and icon resource.
     * @return a {@link CategoryEntity} instance ready for persistence.
     */
    @Override
    public CategoryEntity toEntity(CategoryRequestDTO request) {
        return CategoryEntity.builder()
                .name(request.name())
                .iconResource(request.iconResource())
                .build();
    }

    /**
     * Maps a {@link CategoryEntity} to a {@link CategoryResponseDTO}.
     *
     * @param entity the source category entity from the database.
     * @return a {@link CategoryResponseDTO} containing the category's public data.
     */
    @Override
    public CategoryResponseDTO toResponse(CategoryEntity entity) {
        return new CategoryResponseDTO(
                entity.getId(),
                entity.getName(),
                entity.getIconResource(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
