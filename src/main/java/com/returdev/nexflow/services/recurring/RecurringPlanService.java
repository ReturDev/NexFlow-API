package com.returdev.nexflow.services.recurring;

import com.returdev.nexflow.dto.request.RecurringPlanRequestDTO;
import com.returdev.nexflow.dto.request.update.RecurringPlanUpdateDTO;
import com.returdev.nexflow.dto.response.RecurringPlanResponseDTO;
import com.returdev.nexflow.model.entities.RecurringPlanEntity;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;

/**
 * Service interface for managing automated, recurring financial plans.
 */
@Validated
public interface RecurringPlanService {

    /**
     * Retrieves all plans scheduled for execution based on a specific date.
     *
     * @param executionDate the reference date and time to check against.
     * @param pageable      pagination information.
     * @return a page of entities ready to be processed.
     */
    Page<RecurringPlanEntity> getPlansToExecute(LocalDateTime executionDate, Pageable pageable);

    /**
     * Fetches a specific plan by its ID.
     *
     * @param id the plan identifier.
     * @return the mapped response DTO.
     */
    RecurringPlanResponseDTO getRecurringPlanById(Long id);

    /**
     * Lists all plans associated with a specific wallet, ensuring the caller has access.
     *
     * @param walletId the wallet to filter by.
     * @param pageable pagination parameters.
     * @return a paginated list of plans.
     */
    Page<RecurringPlanResponseDTO> getRecurringPlansByWalletId(Long walletId, Pageable pageable);

    /**
     * Administrative method to retrieve all plans across the system.
     *
     * @param pageable pagination parameters.
     * @return all plans in the system.
     */
    Page<RecurringPlanResponseDTO> getRecurringPlans(Pageable pageable);

    /**
     * Creates and schedules a new recurring financial plan.
     *
     * @param request the data required to create the plan.
     * @return the created plan details.
     */
    RecurringPlanResponseDTO saveRecurringPlan(@Valid RecurringPlanRequestDTO request);

    /**
     * Updates the details or frequency of an existing plan.
     *
     * @param id     the ID of the plan to modify.
     * @param update the updated fields.
     * @return the modified plan.
     */
    RecurringPlanResponseDTO updateRecurringPlan(Long id, @Valid RecurringPlanUpdateDTO update);

    /**
     * Suspends a plan's execution without deleting it.
     *
     * @param planId the ID of the plan to disable.
     * @return the updated plan status.
     */
    RecurringPlanResponseDTO deactivatePlan(Long planId);

    /**
     * Re-activates a suspended plan and recalculates the next execution date.
     *
     * @param planId the ID of the plan to enable.
     * @return the updated plan status.
     */
    RecurringPlanResponseDTO activatePlan(Long planId);

    /**
     * Permanently removes a plan from the system.
     *
     * @param id the ID of the plan to delete.
     */
    void deletePlan(Long id);

    /**
     * Executes the business logic of a plan: creates a transaction and updates
     * the plan's schedule or marks it as {@code ENDED}.
     *
     * @param entity the plan to process.
     */
    void executePlan(@Valid RecurringPlanEntity entity);
}
