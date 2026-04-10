package com.returdev.nexflow.services.recurring;

import com.returdev.nexflow.dto.request.RecurringPlanRequestDTO;
import com.returdev.nexflow.dto.request.update.RecurringPlanUpdateDTO;
import com.returdev.nexflow.dto.response.RecurringPlanResponseDTO;
import com.returdev.nexflow.mappers.RecurringPlanMapper;
import com.returdev.nexflow.model.entities.RecurringPlanEntity;
import com.returdev.nexflow.model.entities.TransactionEntity;
import com.returdev.nexflow.model.enums.PlanStatus;
import com.returdev.nexflow.model.exceptions.ResourceNotFoundException;
import com.returdev.nexflow.repositories.RecurringPlanRepository;
import com.returdev.nexflow.services.transaction.TransactionService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Implementation of {@link RecurringPlanService} that manages time-based triggers.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecurringPlanServiceImpl implements RecurringPlanService {

    private final TransactionService transactionService;
    private final RecurringPlanRepository repository;
    private final RecurringPlanMapper mapper;
    private final RecurringPlanHelper helper;

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<RecurringPlanEntity> getPlansToExecute(LocalDateTime executionDate, Pageable pageable) {
        return repository.findPlansToExecute(
                executionDate,
                pageable
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RecurringPlanResponseDTO getRecurringPlanById(Long id) {
        return mapper.toResponse(
                findRecurringPlanOrThrow(id)
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<RecurringPlanResponseDTO> getRecurringPlansByWalletId(Long walletId, Pageable pageable) {
        return repository.findAllByWalletId(
                walletId,
                pageable
        ).map(mapper::toResponse);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<RecurringPlanResponseDTO> getRecurringPlans(Pageable pageable) {
        return repository.findAll(pageable)
                .map(mapper::toResponse);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public RecurringPlanResponseDTO saveRecurringPlan(RecurringPlanRequestDTO request) {

        RecurringPlanEntity entity = mapper.toEntity(request);

        helper.verifyDates(entity.getStartDate(), entity.getEndDate());

        LocalDateTime nextExecutionDate = helper.calculateNextExecutionDate(entity);
        entity.setNextExecutionDate(nextExecutionDate);

        return mapper.toResponse(
                repository.save(entity)
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public RecurringPlanResponseDTO updateRecurringPlan(Long id, RecurringPlanUpdateDTO update) {

        RecurringPlanEntity dbEntity = findRecurringPlanOrThrow(id);

        mapper.updateEntity(update, dbEntity);

        if (update.startDate() != null || update.endDate() != null) {
            helper.verifyDates(dbEntity.getStartDate(), dbEntity.getEndDate());
        }


        if (update.startDate() != null || update.frequency() != null || update.endDate() != null || update.interval() != null) {

            LocalDateTime nextExecutionDate = helper.calculateNextExecutionDate(dbEntity);
            dbEntity.setNextExecutionDate(nextExecutionDate);

            helper.verifyNextExecutionDateOnChanges(dbEntity);

        }

        return mapper.toResponse(
                repository.save(dbEntity)
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public RecurringPlanResponseDTO deactivatePlan(Long planId) {

        RecurringPlanEntity dbEntity = findRecurringPlanOrThrow(planId);
        dbEntity.setStatus(PlanStatus.INACTIVE);

        return mapper.toResponse(
                repository.save(dbEntity)
        );

    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public RecurringPlanResponseDTO activatePlan(Long planId) {

        RecurringPlanEntity dbEntity = findRecurringPlanOrThrow(planId);

        LocalDateTime nextExecution = helper.calculateNextExecutionDate(dbEntity);

        dbEntity.setStatus(PlanStatus.ACTIVE);
        dbEntity.setNextExecutionDate(nextExecution);

        helper.verifyNextExecutionDateOnChanges(dbEntity);

        return mapper.toResponse(
                repository.save(dbEntity)
        );

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deletePlan(Long id) {

        RecurringPlanEntity dbEntity = findRecurringPlanOrThrow(id);

        repository.delete(dbEntity);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void executePlan(RecurringPlanEntity recurringPlan) {

        TransactionEntity transactionEntity = mapper.toTransactionEntity(recurringPlan);

        transactionService.saveTransactionFromPlan(transactionEntity);

        LocalDateTime nextExecutionDate = helper.calculateNextExecutionDate(recurringPlan);

        if (recurringPlan.getEndDate() != null && recurringPlan.getEndDate().isBefore(nextExecutionDate)) {
            recurringPlan.setStatus(PlanStatus.ENDED);
        }

        recurringPlan.setNextExecutionDate(nextExecutionDate);
        recurringPlan.setLastExecutionDate(transactionEntity.getDate());

        repository.save(recurringPlan);

    }

    /**
     * Internal helper to find a recurring plan or throw a standardized exception.
     *
     * @param id the unique identifier of the recurring plan.
     * @return the found {@link RecurringPlanEntity}.
     * @throws ResourceNotFoundException if no plan exists with the given ID.
     */
    private RecurringPlanEntity findRecurringPlanOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("exception.recurring.not_found"));
    }


}
