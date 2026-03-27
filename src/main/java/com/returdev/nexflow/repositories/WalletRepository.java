package com.returdev.nexflow.repositories;

import com.returdev.nexflow.model.entities.WalletEntity;
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
     * Retrieves a list of all wallet entities associated with a specific user.
     *
     * @param userId the unique identifier of the user whose wallets are to be retrieved.
     * @return a {@link List} of {@link WalletEntity} belonging to the specified user;
     * will be empty if no wallets are found.
     */
    List<WalletEntity> findAllByUserId(UUID userId);

}
