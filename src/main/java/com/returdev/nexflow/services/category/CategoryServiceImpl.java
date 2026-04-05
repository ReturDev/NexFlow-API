package com.returdev.nexflow.services.category;

import com.returdev.nexflow.dto.request.CategoryRequestDTO;
import com.returdev.nexflow.dto.request.update.CategoryUpdateDTO;
import com.returdev.nexflow.dto.response.CategoryResponseDTO;
import com.returdev.nexflow.mappers.CategoryMapper;
import com.returdev.nexflow.model.entities.CategoryEntity;
import com.returdev.nexflow.model.exceptions.FieldAlreadyExistException;
import com.returdev.nexflow.model.exceptions.ResourceNotFoundException;
import com.returdev.nexflow.repositories.CategoryRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Primary implementation of {@link CategoryService}.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository repository;
    private final CategoryMapper mapper;

    /**
     * {@inheritDoc}
     */
    @Override
    public CategoryResponseDTO getCategoryById(Long id) {
        return mapper.toResponse(
                findCategoryOrThrow(id)
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<CategoryResponseDTO> getCategories(Pageable pageable) {
        return repository.findAll(pageable)
                .map(mapper::toResponse);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public CategoryResponseDTO saveCategory(@Valid CategoryRequestDTO category) {

        if (repository.existsByName(category.name())){
            throw new FieldAlreadyExistException("exception.category.name_already_exists", category.name());
        }

        return mapper.toResponse(
                repository.save(
                        mapper.toEntity(category)
                )
        );

    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public CategoryResponseDTO updateCategory(Long id, @Valid CategoryUpdateDTO category) {
        CategoryEntity dbCategory = findCategoryOrThrow(id);

        mapper.updateEntity(category, dbCategory);

        return mapper.toResponse(
                repository.save(dbCategory)
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void deleteCategory(Long id) {
        CategoryEntity category = findCategoryOrThrow(id);
        repository.delete(category);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void verifyCategoryExists(Long id) {
        if (!repository.existsById(id)){
            throw new ResourceNotFoundException("exception.category.not_found");
        }
    }

    /**
     * Internal helper to retrieve a category by ID or terminate with a standardized exception.
     *
     * @param id the unique identifier of the category to be retrieved.
     * @return the {@link CategoryEntity} if found in the persistence context.
     * @throws EntityNotFoundException if no category exists with the provided ID,
     *                                 using the {@code "exception.category.not_found"} message key.
     */
    private CategoryEntity findCategoryOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("exception.category.not_found"));
    }

}