package com.returdev.nexflow.services.recurring;

import com.returdev.nexflow.dto.request.RecurringPlanRequestDTO;
import com.returdev.nexflow.dto.request.update.RecurringPlanUpdateDTO;
import com.returdev.nexflow.dto.response.RecurringPlanResponseDTO;
import com.returdev.nexflow.mappers.RecurringPlanMapper;
import com.returdev.nexflow.model.entities.RecurringPlanEntity;
import com.returdev.nexflow.model.entities.TransactionEntity;
import com.returdev.nexflow.model.entities.UserEntity;
import com.returdev.nexflow.model.enums.PlanStatus;
import com.returdev.nexflow.model.enums.Role;
import com.returdev.nexflow.model.exceptions.ResourceNotFoundException;
import com.returdev.nexflow.model.facade.AuthenticationFacade;
import com.returdev.nexflow.repositories.RecurringPlanRepository;
import com.returdev.nexflow.repositories.WalletRepository;
import com.returdev.nexflow.services.transaction.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Implementation of {@link RecurringPlanService} that manages time-based triggers.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecurringPlanServiceImpl implements RecurringPlanService {

    private final TransactionService transactionService;
    private final RecurringPlanRepository repository;
    private final WalletRepository walletRepository;
    private final RecurringPlanMapper mapper;
    private final RecurringPlanHelper helper;

    private final AuthenticationFacade authenticationFacade;

    /**
     * {@inheritDoc}
     *
     * @apiNote This method is intended for internal scheduling processes
     * (e.g., background jobs) and should not be exposed through public API endpoints.
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
                findByIdWithVerification(id)
        );

    }

    /**
     * {@inheritDoc}
     *
     * @throws ResourceNotFoundException if the target is not found or access is denied.
     */
    @Override
    public Page<RecurringPlanResponseDTO> getRecurringPlansByWalletId(Long walletId, Pageable pageable) {

        final Page<RecurringPlanEntity> page;

        UserEntity authUser = authenticationFacade.getAuthenticateUser();

        if (authUser.getRole() == Role.ADMIN) {
            page = repository.findAllByWalletId(walletId, pageable);
        } else {
            page = repository.findAllByWalletIdAndWalletUserId(walletId, authUser.getId(), pageable);
        }

        return page.map(mapper::toResponse);
    }

    /**
     * {@inheritDoc}
     *
     * @apiNote This method does not perform ownership or security verification.
     * Access control (e.g., ADMIN role check) must be managed at the controller
     * or via security expressions.
     */
    @Override
    public Page<RecurringPlanResponseDTO> getRecurringPlans(Pageable pageable) {
        return repository.findAll(pageable)
                .map(mapper::toResponse);
    }

    /**
     * {@inheritDoc}
     *
     * @throws ResourceNotFoundException if the target is not found or access is denied.
     */
    @Override
    @Transactional
    public RecurringPlanResponseDTO saveRecurringPlan(RecurringPlanRequestDTO request) {

        Long walletId = request.walletId();
        UserEntity authUser = authenticationFacade.getAuthenticateUser();

        if (authUser.getRole() != Role.ADMIN && !walletRepository.existsByIdAndUserId(walletId, authUser.getId())) {
            throw new ResourceNotFoundException("exception.wallet.not_found");
        }

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
     *
     * @throws ResourceNotFoundException if the target is not found or access is denied.
     */
    @Override
    @Transactional
    public RecurringPlanResponseDTO updateRecurringPlan(Long id, RecurringPlanUpdateDTO update) {

        final RecurringPlanEntity dbEntity = findByIdWithVerification(id);

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
     *
     * @throws ResourceNotFoundException if the target is not found or access is denied.
     */
    @Override
    @Transactional
    public RecurringPlanResponseDTO deactivatePlan(Long planId) {

        final RecurringPlanEntity dbEntity = findByIdWithVerification(planId);

        dbEntity.setStatus(PlanStatus.INACTIVE);

        return mapper.toResponse(
                repository.save(dbEntity)
        );

    }

    /**
     * {@inheritDoc}
     *
     * @throws ResourceNotFoundException if the target is not found or access is denied.
     */
    @Override
    @Transactional
    public RecurringPlanResponseDTO activatePlan(Long planId) {

        final RecurringPlanEntity dbEntity = findByIdWithVerification(planId);

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
     *
     * @throws ResourceNotFoundException if the target is not found or access is denied.
     */
    @Override
    public void deletePlan(Long id) {

        repository.delete(
                findByIdWithVerification(id)
        );

    }

    /**
     * {@inheritDoc}
     *
     * @apiNote This method is for internal use by the plan execution engine.
     * It bypasses standard DTO mapping to operate directly on the domain entity.
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
     * Internal security gatekeeper to retrieve a plan based on the current user's identity and role.
     *
     * @param planId the ID of the plan to fetch.
     * @return the verified {@link RecurringPlanEntity}.
     */
    private RecurringPlanEntity findByIdWithVerification(Long planId) {
        UserEntity authUser = authenticationFacade.getAuthenticateUser();

        if (authUser.getRole() == Role.ADMIN) {
            return findRecurringPlanOrThrow(planId);
        }

        return findRecurringPlanOfUserOrThrow(planId, authUser.getId());

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

    /**
     * Internal helper to find a recurring plan belonging to a specific user.
     *
     * @param planId the plan identifier.
     * @param userId the owner's UUID.
     * @return the verified {@link RecurringPlanEntity}.
     * @throws ResourceNotFoundException if the plan is not found or ownership is invalid.
     */
    private RecurringPlanEntity findRecurringPlanOfUserOrThrow(Long planId, UUID userId) {
        return repository.findByIdAndWalletUserId(planId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("exception.recurring.not_found"));
    }


}
