package com.returdev.nexflow.controllers.category;

import com.returdev.nexflow.annotations.swagger.*;
import com.returdev.nexflow.dto.request.CategoryRequestDTO;
import com.returdev.nexflow.dto.request.update.CategoryUpdateDTO;
import com.returdev.nexflow.dto.response.CategoryResponseDTO;
import com.returdev.nexflow.dto.response.wrapper.ContentWrapperResponseDTO;
import com.returdev.nexflow.dto.response.wrapper.PaginationWrapperResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

@InternalServerErrorResponseCode
@UnauthorizedResponseCode
@ForbiddenResponseCode
@Tag(name = "Categories", description = "Endpoints for managing financial categories")
public interface CategoryApi {

    @Operation(
            summary = "Get category by ID",
            description = "Retrieves detailed information of a specific category."
    )
    @OkResponseCode
    @NotFoundResponseCode
    ResponseEntity<ContentWrapperResponseDTO<CategoryResponseDTO>> getCategoryById(
            @Parameter(description = "The unique ID of the category", required = true) Long id);

    @Operation(
            summary = "Get all categories",
            description = "Retrieves a paginated list of all available categories."
    )
    @OkResponseCode
    ResponseEntity<PaginationWrapperResponseDTO<CategoryResponseDTO>> getCategories(
            @Parameter(description = "Pagination information (page, size, sort)") Pageable pageable);

    @Operation(
            summary = "Create a new category",
            description = "Registers a new financial category in the system."
    )
    @CreatedResponseCode
    @BadRequestResponseCode
    @ConflictResponseCode
    ResponseEntity<ContentWrapperResponseDTO<CategoryResponseDTO>> saveCategory(
            @Valid CategoryRequestDTO categoryRequestDTO);

    @Operation(
            summary = "Update category",
            description = "Updates the details of an existing category by its ID."
    )
    @OkResponseCode
    @BadRequestResponseCode
    @NotFoundResponseCode
    ResponseEntity<ContentWrapperResponseDTO<CategoryResponseDTO>> updateCategory(
            @Parameter(description = "The unique ID of the category", required = true) Long id,
            @Valid CategoryUpdateDTO categoryUpdateDTO);

    @Operation(
            summary = "Delete category",
            description = "Removes a category from the system. Note: This may fail if the category is currently in use by transactions."
    )
    @NoContentResponseCode
    @NotFoundResponseCode
    @ConflictResponseCode
    ResponseEntity<Void> deleteCategory(
            @Parameter(description = "The unique ID of the category", required = true) Long id);

}
