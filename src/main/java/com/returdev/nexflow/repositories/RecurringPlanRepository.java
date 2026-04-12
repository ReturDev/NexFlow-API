package com.returdev.nexflow.repositories;

import com.returdev.nexflow.model.entities.RecurringPlanEntity;
import com.returdev.nexflow.model.enums.PlanStatus;
import com.returdev.nexflow.model.enums.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;


/**
 * Repository interface for managing {@link RecurringPlanEntity}.
 * <p>
 * Handles persistence logic for scheduled financial plans, including
 * specialized filtering and execution-ready plan retrieval.
 */
@Repository
public interface RecurringPlanRepository extends JpaRepository<RecurringPlanEntity, Long> {

    /**
     * Retrieves a paginated list of all recurring plans associated with a specific wallet.
     *
     * @param walletId the ID of the wallet.
     * @param pageable pagination and sorting information.
     * @return a {@link Page} of recurring plans for the given wallet.
     */
    Page<RecurringPlanEntity> findAllByWalletId(Long walletId, Pageable pageable);


    /**
     * Finds active plans that are due or overdue for execution based on the current timestamp.
     *
     * @param now      the current reference date and time.
     * @param pageable pagination and sorting information.
     * @return a {@link Page} of plans where {@code status} is true and the execution date has passed.
     */
    @Query("SELECT p FROM RecurringPlanEntity p WHERE p.status = PlanStatus.ACTIVE AND p.nextExecutionDate <= :now ORDER BY p.nextExecutionDate ASC")
    Page<RecurringPlanEntity> findPlansToExecute(
            @Param("now") LocalDateTime now,
            Pageable pageable
    );

    /**
     * Retrieves a paginated list of all recurring plans associated with a specific wallet.
     *
     * @param walletId the internal identifier of the wallet.
     * @param userId   the globally unique identifier of the user who must own the wallet.
     * @param pageable pagination and sorting parameters.
     * @return a {@link Page} of recurring plan entities.
     */
    Page<RecurringPlanEntity> findAllByWalletIdAndWalletUserId(
            @Param("walletId") Long walletId,
            @Param("userId") UUID userId,
            Pageable pageable
    );

    /**
     * Performs a filtered search of recurring plans with dynamic criteria.
     *
     * @param userId     the unique ID of the user for ownership verification.
     * @param walletId   the ID of the target wallet.
     * @param categoryId (Optional) filter by a specific financial category.
     * @param type       (Optional) filter by {@link TransactionType}.
     * @param status     (Optional) filter by {@link PlanStatus}.
     * @param pageable   pagination and sorting information.
     * @return a paginated result set matching the combined filter criteria.
     */
    @Query("SELECT p FROM RecurringPlanEntity p " +
            "WHERE p.wallet.id = :walletId " +
            "AND p.wallet.user.id = :userId " +
            "AND (:categoryId IS NULL OR p.category.id = :categoryId) " +
            "AND (:type IS NULL OR p.type = :type) " +
            "AND (:status IS NULL OR p.status = :status)")
    Page<RecurringPlanEntity> findFilteredPlansByUserID(
            @Param("userId") UUID userId,
            @Param("walletId") Long walletId,
            @Param("categoryId") Long categoryId,
            @Param("type") TransactionType type,
            @Param("status") PlanStatus status,
            Pageable pageable
    );

    /**
     * Performs a multi-criteria search for recurring plans within a specific wallet.
     *
     * @param walletId   the ID of the wallet (required).
     * @param categoryId the ID of the category (optional).
     * @param type       the {@link TransactionType} (optional).
     * @param status     the status of the plan (optional).
     * @param pageable   pagination and sorting information.
     * @return a {@link Page} of plans matching the non-null criteria.
     */
    @Query("SELECT p FROM RecurringPlanEntity p " +
            "WHERE p.wallet.id = :walletId " +
            "AND (:categoryId IS NULL OR p.category.id = :categoryId) " +
            "AND (:type IS NULL OR p.type = :type) " +
            "AND (:status IS NULL OR p.status = :status)")
    Page<RecurringPlanEntity> findFilteredPlans(
            @Param("walletId") Long walletId,
            @Param("categoryId") Long categoryId,
            @Param("type") TransactionType type,
            @Param("status") PlanStatus status,
            Pageable pageable
    );

    /**
     * Retrieves a specific recurring plan only if it belongs to the authorized user.
     *
     * @param planId the unique identifier of the recurring plan.
     * @param userId the unique identifier of the user who must own the associated wallet.
     * @return an {@link Optional} containing the found plan, or empty if no plan matches
     * the ID or if the user does not have permission to access it.
     */
    Optional<RecurringPlanEntity> findByIdAndWalletUserId(
            Long planId,
            UUID userId
    );

}
