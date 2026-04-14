package com.returdev.nexflow.repositories;

import com.returdev.nexflow.model.entities.TransactionEntity;
import com.returdev.nexflow.model.enums.TransactionStatus;
import com.returdev.nexflow.model.enums.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for managing {@link TransactionEntity} persistence.
 * <p>
 * Provides methods for paginated retrieval of financial transactions, including
 * specialized filtering by wallet, category, type, and status.
 */
@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> {

    /**
     * Retrieves a specific transaction while verifying ownership of the associated wallet.
     *
     * @param walletId the unique identifier of the wallet.
     * @param userId   the UUID of the user who must own the wallet.
     * @return an {@link Optional} containing the found {@link TransactionEntity},
     * or empty if no match is found for that specific user.
     */
    Optional<TransactionEntity> findByIdAndWalletUserId(
            Long walletId,
            UUID userId
    );

    /**
     * Retrieves a paginated list of all transactions belonging to a specific wallet.
     *
     * @param walletId the unique identifier of the wallet.
     * @param pageable pagination and sorting parameters (e.g., page number, size, sort).
     * @return a {@link Page} of transactions associated with the given wallet.
     */
    Page<TransactionEntity> findAllByWalletId(
            Long walletId,
            Pageable pageable
    );

    /**
     * Retrieves all transactions for a specific wallet, scoped to a specific user.
     *
     * @param walletId the unique identifier of the wallet.
     * @param userId   the UUID of the owner to ensure data isolation.
     * @param pageable pagination and sorting information.
     * @return a {@link Page} of {@link TransactionEntity} belonging to the user's wallet.
     */
    Page<TransactionEntity> findAllByWalletIdAndWalletUserId(
            Long walletId,
            UUID userId,
            Pageable pageable
    );

    /**
     * Finds transactions based on multiple optional filter criteria.
     * <p>
     * This method uses a custom JPQL query to handle optional parameters. If any
     * parameter (except {@code walletId}) is {@code null}, that specific filter
     * is ignored, allowing for dynamic searching.
     *
     * @param walletId   the ID of the wallet (required).
     * @param categoryId the ID of the category (optional).
     * @param type       the {@link TransactionType} of the transaction (optional).
     * @param status     the {@link TransactionStatus} of the transaction (optional).
     * @param pageable   pagination and sorting information.
     * @return a {@link Page} of transactions matching the specified criteria.
     */
    @Query("SELECT t FROM TransactionEntity t" +
            " WHERE t.wallet.id = :walletId" +
            " AND (:type IS NULL OR :type = t.type)" +
            " AND (:status IS NULL OR :status = t.status)" +
            " AND (:categoryId IS NULL OR :categoryId = t.category.id)")
    Page<TransactionEntity> findFilteredTransactions(
            @Param("walletId") Long walletId,
            @Param("categoryId") Long categoryId,
            @Param("type") TransactionType type,
            @Param("status") TransactionStatus status,
            Pageable pageable
    );

//    /**
//     * Executes a dynamic filtered search for transactions within a specific user's wallet.
//     * <p>
//     * This method uses a custom JPQL query to handle optional parameters. If any
//     * parameter (except {@code walletId}) is {@code null}, that specific filter
//     * is ignored, allowing for dynamic searching.
//     *
//     * @param walletId   the target wallet identifier (Required).
//     * @param userId     the owner's UUID to prevent unauthorized access (Required).
//     * @param categoryId the ID of the category to filter by (Optional).
//     * @param type       the {@link TransactionType} (INCOME/EXPENSE) to filter by (Optional).
//     * @param status     the {@link TransactionStatus} (PENDING/COMPLETED) to filter by (Optional).
//     * @param pageable   pagination and sorting information.
//     * @return a {@link Page} of transactions matching the specified filters.
//     */
//    @Query("SELECT t FROM TransactionEntity t" +
//            " WHERE t.wallet.id = :walletId" +
//            " AND t.wallet.user.id = :userId" +
//            " AND (:type IS NULL OR :type = t.type)" +
//            " AND (:status IS NULL OR :status = t.status)" +
//            " AND (:categoryId IS NULL OR :categoryId = t.category.id)")
//    Page<TransactionEntity> findFilteredTransactions(
//            @Param("walletId") Long walletId,
//            @Param("userId") UUID userId,
//            @Param("categoryId") Long categoryId,
//            @Param("type") TransactionType type,
//            @Param("status") TransactionStatus status,
//            Pageable pageable
//    );

}
