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
@RequestMapping("/categories")
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

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();
        ;

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

        return ResponseEntity.ok()
                .body(
                        ContentWrapperResponseDTO.of(categoryService.updateCategory(id, categoryUpdateDTO))
                );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(
            @PathVariable Long id
    ) {

        categoryService.deleteCategory(id);

        return ResponseEntity.noContent().build();

    }

}
