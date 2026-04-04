package com.returdev.nexflow.mappers;

import com.returdev.nexflow.dto.request.CategoryRequestDTO;
import com.returdev.nexflow.dto.request.update.CategoryUpdateDTO;
import com.returdev.nexflow.dto.response.CategoryResponseDTO;
import com.returdev.nexflow.model.entities.CategoryEntity;
import com.returdev.nexflow.utils.TestDtoFactory;
import com.returdev.nexflow.utils.TestEntityFactory;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CategoryMapperTest {


    private final CategoryMapper mapper = new CategoryMapper();


    @Test
    void toEntity_ShouldMapAllFields() {

        CategoryRequestDTO request = TestDtoFactory.createValidCategoryRequestDTO();

        CategoryEntity entity = mapper.toEntity(request);


        assertThat(entity.getName()).isEqualTo(request.name());
        assertThat(entity.getIconResource()).isEqualTo(request.iconResource());

    }

    @Test
    void toResponse_ShouldMapAllFields() {

        CategoryEntity entity = TestEntityFactory.createValidCategory();

        CategoryResponseDTO response = mapper.toResponse(entity);

        assertThat(response.id()).isEqualTo(entity.getId());
        assertThat(response.name()).isEqualTo(entity.getName());
        assertThat(response.iconResource()).isEqualTo(entity.getIconResource());
        assertThat(response.createdAt()).isEqualTo(entity.getCreatedAt());
        assertThat(response.updatedAt()).isEqualTo(entity.getUpdatedAt());

    }

    @Test
    void updateEntity_WithAllFields_UpdatesCorrectFields() {

        CategoryEntity entity = TestEntityFactory.createValidCategory();
        ;

        CategoryUpdateDTO dto = TestDtoFactory.createValidCategoryUpdateDTO();

        mapper.updateEntity(dto, entity);

        assertThat(entity.getName()).isEqualTo(dto.name());
        assertThat(entity.getIconResource()).isEqualTo(dto.iconResource());

    }

    @Test
    void updateEntity_WithNullFields_DoesNotOverride() {

        String originalName = "Category";
        String originalIconResource = "icon_resource";

        CategoryEntity entity = CategoryEntity.builder().name(originalName).iconResource(originalIconResource).build();

        CategoryUpdateDTO dto = new CategoryUpdateDTO(null, null);

        mapper.updateEntity(dto, entity);

        assertThat(entity.getName()).isEqualTo(originalName);
        assertThat(entity.getIconResource()).isEqualTo(originalIconResource);

    }

}