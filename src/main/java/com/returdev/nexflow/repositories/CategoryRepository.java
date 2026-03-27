package com.returdev.nexflow.repositories;

import com.returdev.nexflow.model.entities.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for {@link CategoryEntity} instances.
 */
@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {

    /**
     * Retrieves a category by its exact name.
     *
     * @param name the unique string name of the category to search for.
     * @return an {@link Optional} containing the found category,
     * or {@link Optional#empty()} if no match exists.
     */
    Optional<CategoryEntity> findByName(String name);

    /**
     * Checks for the existence of a category with a specific name.
     *
     * @param name the name to check in the database.
     * @return {@code true} if a category with the given name exists,
     * {@code false} otherwise.
     */
    boolean existsByName(String name);

}
