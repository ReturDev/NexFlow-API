package com.returdev.nexflow.repositories;

import com.returdev.nexflow.model.entities.RecurringPlanEntity;
import com.returdev.nexflow.model.enums.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;


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
     * @param id the ID of the wallet.
     * @param pageable pagination and sorting information.
     * @return a {@link Page} of recurring plans for the given wallet.
     */
    Page<RecurringPlanEntity> findAllByWalletId(Long id, Pageable pageable);

    /**
     * Finds active plans that are due or overdue for execution based on the current timestamp.
     *
     * @param now the current reference date and time.
     * @param pageable pagination and sorting information.
     * @return a {@link Page} of plans where {@code isActive} is true and the execution date has passed.
     */
    @Query("SELECT p FROM RecurringPlanEntity p WHERE p.isActive = true AND p.nextExecutionDate <= :now")
    Page<RecurringPlanEntity> findPlansToExecute(
            @Param("now") LocalDateTime now,
            Pageable pageable
    );

    /**
     * Performs a multi-criteria search for recurring plans within a specific wallet.
     * <p>
     * All parameters except {@code walletId} are optional. If a parameter is {@code null},
     * that specific filter is ignored in the resulting query.
     *
     * @param walletId the ID of the wallet (required).
     * @param categoryId the ID of the category (optional).
     * @param type the {@link TransactionType} (optional).
     * @param active the status of the plan (optional).
     * @param pageable pagination and sorting information.
     * @return a {@link Page} of plans matching the non-null criteria.
     */
    @Query("SELECT p FROM RecurringPlanEntity p " +
            "WHERE p.wallet.id = :walletId " +
            "AND (:categoryId IS NULL OR p.category.id = :categoryId) " +
            "AND (:type IS NULL OR p.type = :type) " +
            "AND (:active IS NULL OR p.isActive = :active)")
    Page<RecurringPlanEntity> findFilteredPlans(
            @Param("walletId") Long walletId,
            @Param("categoryId") Long categoryId,
            @Param("type") TransactionType type,
            @Param("active") Boolean active,
            Pageable pageable
    );

}
