package com.returdev.nexflow.services.recurring;

import com.returdev.nexflow.dto.request.RecurringPlanRequestDTO;
import com.returdev.nexflow.dto.request.update.RecurringPlanUpdateDTO;
import com.returdev.nexflow.dto.response.RecurringPlanResponseDTO;
import com.returdev.nexflow.model.entities.RecurringPlanEntity;
import com.returdev.nexflow.model.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
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
     * Retrieves a page of plans that are due for execution based on the provided date.
     *
     * @param executionDate the reference date to check for pending plans.
     * @param pageable      pagination parameters.
     * @return a page of {@link RecurringPlanEntity} ready to be processed.
     */
    Page<RecurringPlanEntity> getPlansToExecute(LocalDateTime executionDate, Pageable pageable);

    /**
     * Retrieves a recurring plan by its unique ID.
     *
     * @param id the plan identifier.
     * @return the found {@link RecurringPlanResponseDTO}.
     * @throws EntityNotFoundException if the plan is not found.
     */
    RecurringPlanResponseDTO getRecurringPlanById(Long id);

    /**
     * Returns all recurring plans associated with a specific wallet.
     *
     * @param walletId the wallet identifier.
     * @param pageable pagination parameters.
     * @return a page of {@link RecurringPlanResponseDTO}.
     */
    Page<RecurringPlanResponseDTO> getRecurringPlansByWalletId(Long walletId, Pageable pageable);

    /**
     * Returns a paginated list of all recurring plans.
     *
     * @param pageable pagination parameters.
     * @return a page of {@link RecurringPlanResponseDTO}.
     */
    Page<RecurringPlanResponseDTO> getRecurringPlans(Pageable pageable);

    /**
     * Creates and schedules a new recurring plan.
     *
     * @param request the plan configuration.
     * @return the persisted {@link RecurringPlanResponseDTO}.
     */
    RecurringPlanResponseDTO saveRecurringPlan(@Valid RecurringPlanRequestDTO request);

    /**
     * Updates an existing plan's configuration.
     *
     * @param id     the plan identifier.
     * @param update the update DTO.
     * @return the modified {@link RecurringPlanResponseDTO}.
     */
    RecurringPlanResponseDTO updateRecurringPlan(Long id, @Valid RecurringPlanUpdateDTO update);

    /**
     * Pauses a plan, preventing further transactions from being generated.
     *
     * @param planId the ID of the plan to deactivate.
     * @return the updated {@link RecurringPlanResponseDTO}.
     */
    RecurringPlanResponseDTO deactivatePlan(Long planId);

    /**
     * Resumes an inactive plan and re-calculates the next valid execution date.
     *
     * @param planId the ID of the plan to activate.
     * @return the updated {@link RecurringPlanResponseDTO}.
     */
    RecurringPlanResponseDTO activatePlan(Long planId);

    /**
     * Removes a wallet from the system.
     *
     * @param id the ID of the transaction to remove.
     * @throws ResourceNotFoundException if the transaction does not exist.
     */
    void deletePlan(Long id);

    /**
     * Executes the logic to generate a new transaction record from a plan.
     *
     * @param entity the plan to be executed.
     */
    void executePlan(@Valid RecurringPlanEntity entity);
}
