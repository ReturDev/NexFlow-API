package com.returdev.nexflow.services.category;

import com.returdev.nexflow.dto.request.CategoryRequestDTO;
import com.returdev.nexflow.dto.request.update.CategoryUpdateDTO;
import com.returdev.nexflow.dto.response.CategoryResponseDTO;
import com.returdev.nexflow.model.exceptions.FieldAlreadyExistException;
import com.returdev.nexflow.model.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;

/**
 * Service interface defining the business contract for Category management.
 */
@Validated
public interface CategoryService {

    /**
     * Retrieves a single category by its unique identifier.
     *
     * @param id the ID of the category to find.
     * @return the found {@link CategoryResponseDTO}.
     * @throws ResourceNotFoundException if no category exists with the given ID.
     */
    CategoryResponseDTO getCategoryById(Long id);

    /**
     * Returns a paginated list of all available categories.
     *
     * @param pageable pagination and sorting information.
     * @return a {@link Page} of {@link CategoryResponseDTO}.
     */
    Page<CategoryResponseDTO> getCategories(Pageable pageable);

    /**
     * Persists a new category in the system.
     *
     * @param category the request DTO containing the category details.
     * @return the persisted {@link CategoryResponseDTO} including generated IDs.
     * @throws FieldAlreadyExistException if a category already exist with the name provided.
     */
    CategoryResponseDTO saveCategory(@Valid CategoryRequestDTO category);

    /**
     * Performs a partial update on an existing category.
     *
     * @param id       the ID of the category to update.
     * @param category the DTO containing the fields to be modified.
     * @return the updated {@link CategoryResponseDTO}.
     * @throws ResourceNotFoundException if the category to update is not found.
     */
    CategoryResponseDTO updateCategory(Long id, @Valid CategoryUpdateDTO category);

    /**
     * Removes a category from the system.
     *
     * @param id the ID of the category to delete.
     * @throws ResourceNotFoundException if the category to delete is not found.
     */
    void deleteCategory(Long id);

    /**
     * Validates the existence of a category by its unique identifier.
     *
     * @param id the unique identifier of the category to verify.
     * @throws ResourceNotFoundException if no category exists with the provided ID.
     */
    void verifyCategoryExists(Long id);
}
