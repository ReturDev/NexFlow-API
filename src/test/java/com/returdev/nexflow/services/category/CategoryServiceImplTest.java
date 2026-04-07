package com.returdev.nexflow.services.category;

import com.returdev.nexflow.dto.request.CategoryRequestDTO;
import com.returdev.nexflow.dto.request.update.CategoryUpdateDTO;
import com.returdev.nexflow.dto.response.CategoryResponseDTO;
import com.returdev.nexflow.mappers.CategoryMapper;
import com.returdev.nexflow.model.entities.CategoryEntity;
import com.returdev.nexflow.model.exceptions.FieldAlreadyExistException;
import com.returdev.nexflow.model.exceptions.ResourceNotFoundException;
import com.returdev.nexflow.repositories.CategoryRepository;
import com.returdev.nexflow.utils.TestDtoFactory;
import com.returdev.nexflow.utils.TestEntityFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository repository;
    @Mock
    private CategoryMapper mapper;
    @InjectMocks
    private CategoryServiceImpl service;

    @Test
    void getCategoryById_WithExistingId_ReturnsCategory() {

        Long id = 1L;
        CategoryEntity entity = TestEntityFactory.createValidCategory();
        CategoryResponseDTO expectedResponse = TestDtoFactory.createValidCategoryResponseDTO();

        when(repository.findById(any())).thenReturn(Optional.of(entity));
        when(mapper.toResponse(entity)).thenReturn(expectedResponse);

        CategoryResponseDTO result = service.getCategoryById(id);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(expectedResponse.id());
        assertThat(result.name()).isEqualTo(expectedResponse.name());

        verify(repository).findById(id);

    }

    @Test
    void getCategoryById_WhenIdNotExist_ShouldThrowException() {

        Long id = 1L;
        when(repository.findById(id)).thenReturn(Optional.empty());


        assertThrows(ResourceNotFoundException.class, () -> service.getCategoryById(id));

        verify(mapper, never()).toResponse(any());
        verify(repository).findById(id);

    }

    @Test
    void getCategories_ReturnsPageOfCategories() {

        CategoryEntity entity = TestEntityFactory.createValidCategory();
        CategoryResponseDTO responseDTO = TestDtoFactory.createValidCategoryResponseDTO();
        Pageable pageable = PageRequest.of(0, 10);

        Page<CategoryEntity> categoryPage = new PageImpl<>(List.of(entity));

        when(repository.findAll(pageable)).thenReturn(categoryPage);
        when(mapper.toResponse(entity)).thenReturn(responseDTO);

        Page<CategoryResponseDTO> result = service.getCategories(pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().id()).isEqualTo(responseDTO.id());
        verify(repository).findAll(pageable);
    }

    @Test
    void saveCategory_WithNewUniqueName_ShouldSaveTheCategory() {

        CategoryEntity entity = TestEntityFactory.createValidCategory();
        CategoryRequestDTO request = TestDtoFactory.createValidCategoryRequestDTO();
        CategoryResponseDTO expectedResponse = TestDtoFactory.createValidCategoryResponseDTO();

        when(mapper.toResponse(any())).thenReturn(expectedResponse);
        when(mapper.toEntity(request)).thenReturn(entity);
        when(repository.existsByName(any())).thenReturn(false);
        when(repository.save(entity)).thenReturn(entity);

        CategoryResponseDTO result = service.saveCategory(request);


        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(expectedResponse.id());
        assertThat(result.name()).isEqualTo(expectedResponse.name());

        verify(repository).save(entity);

    }

    @Test
    void saveCategory_WithNameRepeated_ShouldThrowException() {

        CategoryRequestDTO request = TestDtoFactory.createValidCategoryRequestDTO();

        when(repository.existsByName(any())).thenReturn(true);

        assertThrows(FieldAlreadyExistException.class, () -> service.saveCategory(request));

        verify(mapper, never()).toEntity(any());
        verify(repository, never()).save(any());

    }

    @Test
    void updateCategory_WhenIdExists_ReturnsUpdatedCategory() {

        Long id = 1L;
        CategoryUpdateDTO updateDTO = TestDtoFactory.createValidCategoryUpdateDTO();
        CategoryEntity entity = TestEntityFactory.createValidCategory();
        CategoryResponseDTO expectedResponse = TestDtoFactory.createValidCategoryResponseDTO();

        when(mapper.toResponse(entity)).thenReturn(expectedResponse);
        when(repository.save(entity)).thenReturn(entity);
        when(repository.findById(id)).thenReturn(Optional.of(entity));

        CategoryResponseDTO result = service.updateCategory(id, updateDTO);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(expectedResponse.id());
        assertThat(result.name()).isEqualTo(expectedResponse.name());

        verify(repository).findById(id);
        verify(repository).save(entity);

    }

    @Test
    void updateCategory_WhenIdNotExists_ShouldThrowException() {

        Long id = 1L;
        CategoryUpdateDTO updateDTO = TestDtoFactory.createValidCategoryUpdateDTO();
        CategoryEntity entity = TestEntityFactory.createValidCategory();

        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.updateCategory(id, updateDTO));


        verify(repository).findById(id);
        verify(mapper, never()).updateEntity(updateDTO,entity);
        verify(repository, never()).save(entity);

    }

    @Test
    void deleteCategory_WhenIdExists_ShouldDeleteTheCategory() {

        Long id = 1L;
        CategoryEntity entity = TestEntityFactory.createValidCategory();

        when(repository.findById(id)).thenReturn(Optional.of(entity));

        service.deleteCategory(id);

        verify(repository).findById(id);
        verify(repository).delete(entity);

    }

    @Test
    void deleteCategory_WhenIdNotExists_ShouldThrowException() {

        Long id = 1L;

        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.deleteCategory(id));


        verify(repository).findById(id);
        verify(repository, never()).deleteById(id);

    }

    @Test
    void verifyCategoryExists_WhenIdNotExists_ShouldThrowException() {

        Long id = 1L;

        when(repository.existsById(id)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> service.verifyCategoryExists(id));

        verify(repository).existsById(id);

    }
}