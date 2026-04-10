package com.returdev.nexflow.services.recurring;

import com.returdev.nexflow.dto.request.RecurringPlanRequestDTO;
import com.returdev.nexflow.dto.request.update.RecurringPlanUpdateDTO;
import com.returdev.nexflow.dto.response.RecurringPlanResponseDTO;
import com.returdev.nexflow.mappers.RecurringPlanMapper;
import com.returdev.nexflow.model.entities.RecurringPlanEntity;
import com.returdev.nexflow.model.entities.TransactionEntity;
import com.returdev.nexflow.model.enums.Frequency;
import com.returdev.nexflow.model.enums.PlanStatus;
import com.returdev.nexflow.model.exceptions.DateConflictException;
import com.returdev.nexflow.model.exceptions.ResourceNotFoundException;
import com.returdev.nexflow.repositories.RecurringPlanRepository;
import com.returdev.nexflow.services.transaction.TransactionServiceImpl;
import com.returdev.nexflow.utils.TestDtoFactory;
import com.returdev.nexflow.utils.TestEntityFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecurringPlanServiceImplTest {

    @Mock
    private TransactionServiceImpl transactionService;
    @Mock
    private RecurringPlanRepository repository;
    @Mock
    private RecurringPlanMapper mapper;
    @Mock
    private RecurringPlanHelper helper;
    @InjectMocks
    private RecurringPlanServiceImpl recurringPlanService;

    @Test
    void getPlansToExecute_ReturnsPlanToExecutePage() {

        LocalDateTime timeToExecute = LocalDateTime.now();
        Pageable pageable = Pageable.ofSize(15);
        RecurringPlanEntity entity = TestEntityFactory.createValidRecurringPlan(null,null);
        Page<RecurringPlanEntity> expectedPage = new PageImpl<>(List.of(entity));

        when(repository.findPlansToExecute(timeToExecute, pageable)).thenReturn(expectedPage);

        Page<RecurringPlanEntity> result = recurringPlanService.getPlansToExecute(timeToExecute, pageable);

        assertThat(result)
                .isEqualTo(expectedPage)
                .first()
                .isEqualTo(entity);

        verify(repository).findPlansToExecute(timeToExecute,pageable);

    }

    @Test
    void getRecurringPlanById_WhenPlanExists_ReturnsThePlan() {

        Long planId = 1L;
        RecurringPlanEntity entity = TestEntityFactory.createValidRecurringPlan(null,null);
        RecurringPlanResponseDTO response = TestDtoFactory.createValidPlanResponseDTO();

        when(mapper.toResponse(entity)).thenReturn(response);
        when(repository.findById(planId)).thenReturn(Optional.of(entity));

        RecurringPlanResponseDTO result = recurringPlanService.getRecurringPlanById(planId);

        assertThat(result)
                .isEqualTo(response);

        verify(repository).findById(planId);
        verify(mapper).toResponse(entity);

    }

    @Test
    void getRecurringPlanById_WhenPlanDoesNotExist_ShouldThrowException() {

        Long planId = 1L;

        when(repository.findById(planId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> recurringPlanService.getRecurringPlanById(planId));

        verify(repository).findById(planId);
        verify(mapper, never()).toResponse(any());

    }

    @Test
    void getRecurringPlansByWalletId_WhenWalletExists_ReturnsRecurringPlanPage() {

        Long walletId = 1L;
        Pageable pageable = Pageable.ofSize(15);
        RecurringPlanEntity entity = TestEntityFactory.createValidRecurringPlan(null,null);
        RecurringPlanResponseDTO response = TestDtoFactory.createValidPlanResponseDTO();

        Page<RecurringPlanResponseDTO> expectedPage = new PageImpl<>(List.of(response));


        when(mapper.toResponse(entity)).thenReturn(response);
        when(repository.findAllByWalletId(walletId, pageable)).thenReturn(new PageImpl<>(List.of(entity)));

        Page<RecurringPlanResponseDTO> result = recurringPlanService.getRecurringPlansByWalletId(walletId,pageable);

        assertThat(result)
                .isEqualTo(expectedPage)
                .first()
                .isEqualTo(response);

        verify(repository).findAllByWalletId(walletId, pageable);
        verify(mapper).toResponse(entity);

    }


    @Test
    void getRecurringPlans_ReturnsRecurringPlanPage() {

        Pageable pageable = Pageable.ofSize(15);
        RecurringPlanEntity entity = TestEntityFactory.createValidRecurringPlan(null,null);
        RecurringPlanResponseDTO response = TestDtoFactory.createValidPlanResponseDTO();
        Page<RecurringPlanResponseDTO> expectedPage = new PageImpl<>(List.of(response));

        when(mapper.toResponse(entity)).thenReturn(response);
        when(repository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(entity)));

        Page<RecurringPlanResponseDTO> result = recurringPlanService.getRecurringPlans(pageable);

        assertThat(result)
                .isEqualTo(expectedPage)
                .isNotEmpty()
                .first()
                .isEqualTo(response);

        verify(mapper).toResponse(entity);
        verify(repository).findAll(pageable);

    }

    @Test
    void saveRecurringPlan_WhenAllFieldsValid_ReturnsSavedRecurringPlan() {

        RecurringPlanEntity entity = TestEntityFactory.createValidRecurringPlan(null,null);
        RecurringPlanRequestDTO request = TestDtoFactory.createValidPlanRequestDTO(null,null);
        RecurringPlanResponseDTO expectedResponse = TestDtoFactory.createValidPlanResponseDTO();


        when(mapper.toEntity(request)).thenReturn(entity);
        when(mapper.toResponse(entity)).thenReturn(expectedResponse);
        when(repository.save(entity)).thenReturn(entity);

        RecurringPlanResponseDTO result = recurringPlanService.saveRecurringPlan(request);

        assertThat(result)
                .usingRecursiveAssertion()
                .isEqualTo(expectedResponse);

        verify(helper).verifyDates(any(),any());
        verify(helper).calculateNextExecutionDate(entity);
        verify(repository).save(entity);
        verify(mapper).toResponse(entity);

    }

    @Test
    void saveRecurringPlan_WhenStartDateIsBeforeEndDate_ShouldThrowException() {

        RecurringPlanEntity entity = TestEntityFactory.createValidRecurringPlan(null,null);
        RecurringPlanRequestDTO request = TestDtoFactory.createValidPlanRequestDTO(null,null);

        when(mapper.toEntity(request)).thenReturn(entity);
        doThrow(DateConflictException.class).when(helper).verifyDates(any(),any());


        assertThrows(DateConflictException.class,() ->recurringPlanService.saveRecurringPlan(request));

        verify(helper).verifyDates(any(),any());
        verify(helper, never()).calculateNextExecutionDate(entity);
        verify(repository, never()).save(entity);
        verify(mapper, never()).toResponse(entity);

    }

    @Test
    void updateRecurringPlan_WhenOnlyBasicFieldsChanged_ShouldNotRecalculateDate() {
        Long id = 1L;
        RecurringPlanUpdateDTO update = new RecurringPlanUpdateDTO(
                "Nuevo Nombre",
                "Nueva Desc",
                50L,
                null,
                null,
                null,
                null,
                null,
                1L
        );
        RecurringPlanEntity dbEntity = TestEntityFactory.createValidRecurringPlan(null, null);
        RecurringPlanResponseDTO expectedResponse = TestDtoFactory.createValidPlanResponseDTO();

        when(repository.findById(id)).thenReturn(Optional.of(dbEntity));
        when(repository.save(dbEntity)).thenReturn(dbEntity);
        when(mapper.toResponse(dbEntity)).thenReturn(expectedResponse);

        RecurringPlanResponseDTO result = recurringPlanService.updateRecurringPlan(id, update);

        assertThat(result).isEqualTo(expectedResponse);
        verify(mapper).updateEntity(update, dbEntity);
        verify(helper, never()).verifyDates(any(), any());
        verify(helper, never()).calculateNextExecutionDate(any());
        verify(repository).save(dbEntity);
    }

    @Test
    void updateRecurringPlan_WhenFrequencyChanged_ShouldRecalculateNextExecutionDate() {
        Long id = 1L;
        RecurringPlanUpdateDTO update = new RecurringPlanUpdateDTO(
                null,
                null,
                null,
                null,
                null,
                Frequency.MONTHLY,
                2,
                null,
                null
        );
        RecurringPlanEntity dbEntity = TestEntityFactory.createValidRecurringPlan(null, null);
        LocalDateTime newNextDate = LocalDateTime.now().plusMonths(2);

        when(repository.findById(id)).thenReturn(Optional.of(dbEntity));
        when(helper.calculateNextExecutionDate(dbEntity)).thenReturn(newNextDate);
        when(repository.save(dbEntity)).thenReturn(dbEntity);
        when(mapper.toResponse(dbEntity)).thenReturn(TestDtoFactory.createValidPlanResponseDTO());

        recurringPlanService.updateRecurringPlan(id, update);

        verify(helper).calculateNextExecutionDate(dbEntity);
        verify(helper).verifyNextExecutionDateOnChanges(dbEntity);
        assertThat(dbEntity.getNextExecutionDate()).isEqualTo(newNextDate);
        verify(repository).save(dbEntity);
    }

    @Test
    void updateRecurringPlan_WhenDatesAreInvalid_ShouldThrowDateConflictException() {
        Long id = 1L;
        OffsetDateTime today = OffsetDateTime.now();
        RecurringPlanUpdateDTO update = new RecurringPlanUpdateDTO(
                null,
                null,
                null,
                null,
                today,
                null,
                2,
                today.minusDays(10),
                null
        );
        RecurringPlanEntity dbEntity = TestEntityFactory.createValidRecurringPlan(null, null);

        when(repository.findById(id)).thenReturn(Optional.of(dbEntity));
        doThrow(DateConflictException.class).when(helper).verifyDates(any(), any());

        assertThrows(DateConflictException.class, () -> recurringPlanService.updateRecurringPlan(id, update));

        verify(helper).verifyDates(any(), any());
        verify(repository, never()).save(any());
        verify(mapper, never()).toResponse(any());
    }

    @Test
    void updateRecurringPlan_WhenNewCalculatedDateIsAfterEndDate_ShouldThrowDateConflictException() {
        Long id = 1L;
        RecurringPlanUpdateDTO update = new RecurringPlanUpdateDTO(null,
                null,
                null,
                null,
                null,
                null,
                12,
                null,
                null
        );

        RecurringPlanEntity dbEntity = TestEntityFactory.createValidRecurringPlan(null, null);
        LocalDateTime farFutureDate = LocalDateTime.now().plusYears(5);

        when(repository.findById(id)).thenReturn(Optional.of(dbEntity));

        when(helper.calculateNextExecutionDate(dbEntity)).thenReturn(farFutureDate);

        doThrow(new DateConflictException("exception.recurring_plan.updating_next_execution_error"))
                .when(helper).verifyNextExecutionDateOnChanges(dbEntity);

        assertThrows(DateConflictException.class, () -> recurringPlanService.updateRecurringPlan(id, update));

        verify(helper).calculateNextExecutionDate(dbEntity);
        verify(helper).verifyNextExecutionDateOnChanges(dbEntity);
        verify(repository, never()).save(any());
    }

    @Test
    void deactivatePlan_WhenPlanExists_ShouldSetStatusToInactive() {
        Long planId = 1L;
        RecurringPlanEntity dbEntity = TestEntityFactory.createValidRecurringPlan(null, null);
        dbEntity.setStatus(PlanStatus.ACTIVE);

        when(repository.findById(planId)).thenReturn(Optional.of(dbEntity));
        when(repository.save(dbEntity)).thenReturn(dbEntity);

       recurringPlanService.deactivatePlan(planId);

        assertThat(dbEntity.getStatus()).isEqualTo(PlanStatus.INACTIVE);
        verify(repository).save(dbEntity);
        verify(mapper).toResponse(dbEntity);
    }

    @Test
    void activatePlan_WhenPlanIsInactive_ShouldRecalculateDateAndSetStatusToActive() {

        Long planId = 1L;
        RecurringPlanEntity dbEntity = TestEntityFactory.createValidRecurringPlan(null, null);
        dbEntity.setStatus(PlanStatus.INACTIVE);

        LocalDateTime newNextDate = LocalDateTime.now().plusDays(5);

        when(repository.findById(planId)).thenReturn(Optional.of(dbEntity));
        when(helper.calculateNextExecutionDate(dbEntity)).thenReturn(newNextDate);
        when(repository.save(dbEntity)).thenReturn(dbEntity);

        recurringPlanService.activatePlan(planId);

        assertThat(dbEntity.getStatus()).isEqualTo(PlanStatus.ACTIVE);
        assertThat(dbEntity.getNextExecutionDate()).isEqualTo(newNextDate);
        verify(helper).calculateNextExecutionDate(dbEntity);
        verify(helper).verifyNextExecutionDateOnChanges(dbEntity);
        verify(repository).save(dbEntity);
    }

    @Test
    void activatePlan_WhenCalculatedDateIsAfterEndDate_ShouldThrowException() {
        Long planId = 1L;
        RecurringPlanEntity dbEntity = TestEntityFactory.createValidRecurringPlan(null, null);

        when(repository.findById(planId)).thenReturn(Optional.of(dbEntity));
        when(helper.calculateNextExecutionDate(dbEntity)).thenReturn(LocalDateTime.now().plusYears(10));

        doThrow(DateConflictException.class).when(helper).verifyNextExecutionDateOnChanges(dbEntity);

        assertThrows(DateConflictException.class, () -> recurringPlanService.activatePlan(planId));

        verify(repository, never()).save(any());
    }

    @Test
    void deletePlan_WhenPlanExists_ShouldCallDelete() {
        Long planId = 1L;
        RecurringPlanEntity dbEntity = TestEntityFactory.createValidRecurringPlan(null, null);

        when(repository.findById(planId)).thenReturn(Optional.of(dbEntity));

        recurringPlanService.deletePlan(planId);

        verify(repository).delete(dbEntity);
    }

    @Test
    void executePlan_WhenIsNormalExecution_ShouldSaveTransactionAndUpdatePlan() {
        RecurringPlanEntity plan = TestEntityFactory.createValidRecurringPlan(null, null);
        plan.setEndDate(null);
        TransactionEntity transaction = TestEntityFactory.createValidTransaction(null,null,null);
        transaction.setDate(LocalDateTime.now());

        LocalDateTime nextDate = LocalDateTime.now().plusMonths(1);

        when(mapper.toTransactionEntity(plan)).thenReturn(transaction);
        when(helper.calculateNextExecutionDate(plan)).thenReturn(nextDate);

        recurringPlanService.executePlan(plan);

        verify(transactionService).saveTransactionFromPlan(transaction);
        verify(repository).save(plan);

        assertThat(plan.getNextExecutionDate()).isEqualTo(nextDate);
        assertThat(plan.getLastExecutionDate()).isEqualTo(transaction.getDate());
        assertThat(plan.getStatus()).isEqualTo(PlanStatus.ACTIVE);
    }

    @Test
    void executePlan_WhenNextExecutionIsAfterEndDate_ShouldSetStatusToEnded() {
        LocalDateTime endDate = LocalDateTime.now().plusDays(10);
        RecurringPlanEntity plan = TestEntityFactory.createValidRecurringPlan(null, null);
        plan.setEndDate(endDate);

        TransactionEntity transaction = new TransactionEntity();
        transaction.setDate(LocalDateTime.now());

        LocalDateTime nextDateTooFar = LocalDateTime.now().plusMonths(1);

        when(mapper.toTransactionEntity(plan)).thenReturn(transaction);
        when(helper.calculateNextExecutionDate(plan)).thenReturn(nextDateTooFar);

        recurringPlanService.executePlan(plan);

        assertThat(plan.getStatus()).isEqualTo(PlanStatus.ENDED);
        assertThat(plan.getNextExecutionDate()).isEqualTo(nextDateTooFar);
        verify(repository).save(plan);
    }

}