package com.returdev.nexflow.repositories;

import com.returdev.nexflow.model.entities.UserEntity;
import com.returdev.nexflow.model.entities.WalletEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository interface for {@link WalletEntity} instances.
 */
@Repository
public interface WalletRepository extends JpaRepository<WalletEntity, Long> {

    /**
     * Retrieves a paginated list of all wallets belonging to a specific user.
     *
     * @param userId   the unique identifier (UUID) of the owner.
     * @param pageable the pagination and sorting information (e.g., page number,
     * size, and sort criteria).
     * @return a {@link Page} of wallet entities owned by the user.
     */
    Page<WalletEntity> findAllByUserId(UUID userId, Pageable pageable);

    /**
     * Counts the total number of records associated with a specific user.
     *
     * @param userId the unique {@link java.util.UUID} of the user.
     * @return the total count of entities belonging to the user.
     */
    long countByUserId(UUID userId);

    /**
     * Checks for the existence of a record associated with the specified name.
     *
     * @param name the name string to search for (case-sensitive by default).
     * @return {@code true} if a record with the given name exists, {@code false} otherwise.
     */
    boolean existsByName(String name);

}
