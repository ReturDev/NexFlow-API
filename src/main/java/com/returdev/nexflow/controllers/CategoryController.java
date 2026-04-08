package com.returdev.nexflow.controllers;

import com.returdev.nexflow.dto.request.CategoryRequestDTO;
import com.returdev.nexflow.dto.request.update.CategoryUpdateDTO;
import com.returdev.nexflow.dto.response.CategoryResponseDTO;
import com.returdev.nexflow.dto.response.wrapper.ContentWrapperResponseDTO;
import com.returdev.nexflow.dto.response.wrapper.PaginationWrapperResponseDTO;
import com.returdev.nexflow.services.category.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("category")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/{id}")
    public ResponseEntity<ContentWrapperResponseDTO<CategoryResponseDTO>> getCategoryById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                ContentWrapperResponseDTO.of(categoryService.getCategoryById(id))
        );
    }

    @GetMapping()
    public ResponseEntity<PaginationWrapperResponseDTO<CategoryResponseDTO>> getCategories(
            @Valid Pageable pageable
    ) {
        return ResponseEntity.ok(
                PaginationWrapperResponseDTO.fromPage(
                        categoryService.getCategories(pageable)
                )
        );
    }

    @PostMapping()
    public ResponseEntity<ContentWrapperResponseDTO<CategoryResponseDTO>> saveCategory(
            @RequestBody @Valid CategoryRequestDTO categoryRequestDTO
    ) {

        CategoryResponseDTO response = categoryService.saveCategory(categoryRequestDTO);

        URI location = createLocation(response.id());

        return ResponseEntity.created(
                location
        ).body(
                ContentWrapperResponseDTO.of(response)
        );
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ContentWrapperResponseDTO<CategoryResponseDTO>> updateCategory(
            @PathVariable Long id,
            @RequestBody @Valid CategoryUpdateDTO categoryUpdateDTO
    ) {

        CategoryResponseDTO response = categoryService.updateCategory(id, categoryUpdateDTO);

        URI location = createLocation(response.id());

        return ResponseEntity.ok()
                .location(location)
                .body(
                        ContentWrapperResponseDTO.of(response)
                );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(
            @PathVariable Long id
    ) {

        categoryService.deleteCategory(id);

        return ResponseEntity.noContent().build();

    }

    private URI createLocation(Long id) {
        return ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();
    }


}
