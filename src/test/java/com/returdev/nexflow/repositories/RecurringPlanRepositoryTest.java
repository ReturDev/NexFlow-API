package com.returdev.nexflow.repositories;

import com.returdev.nexflow.model.entities.CategoryEntity;
import com.returdev.nexflow.model.entities.RecurringPlanEntity;
import com.returdev.nexflow.model.entities.UserEntity;
import com.returdev.nexflow.model.entities.WalletEntity;
import com.returdev.nexflow.model.enums.PlanStatus;
import com.returdev.nexflow.model.enums.TransactionType;
import com.returdev.nexflow.utils.TestEntityFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.mysql.MySQLContainer;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class RecurringPlanRepositoryTest {

    @Container
    @ServiceConnection
    static MySQLContainer mysql = new MySQLContainer("mysql:8.0");

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private RecurringPlanRepository repository;

    private WalletEntity defaultWallet;
    private CategoryEntity defaultCategory;
    private UserEntity defaultUser;

    @BeforeEach
    void setUp() {
        defaultUser = entityManager.persist(TestEntityFactory.createValidUser());
        defaultWallet = entityManager.persist(TestEntityFactory.createValidWallet(defaultUser));
        defaultCategory = entityManager.persist(TestEntityFactory.createValidCategory());
    }


    @Test
    void findPlansToExecute_WithValidAndInvalidDates_ReturnsAllValidPlans() {

        LocalDateTime targetExecutionDate = LocalDateTime.of(2026, 4, 4, 0, 0);

        RecurringPlanEntity validPlan = TestEntityFactory.createValidRecurringPlan(defaultCategory, defaultWallet);
        validPlan.setNextExecutionDate(targetExecutionDate);
        entityManager.persist(validPlan);

        RecurringPlanEntity invalidPlan = TestEntityFactory.createValidRecurringPlan(defaultCategory, defaultWallet);
        invalidPlan.setNextExecutionDate(targetExecutionDate.plusDays(1));
        entityManager.persist(invalidPlan);

        entityManager.flush();
        entityManager.clear();


        Page<RecurringPlanEntity> result = repository.findPlansToExecute(targetExecutionDate, Pageable.ofSize(15));
        List<RecurringPlanEntity> content = result.getContent();

        assertThat(content)
                .hasSize(1)
                .extracting(RecurringPlanEntity::getId)
                .containsExactly(validPlan.getId());

    }

    @Test
    void findPlansToExecute_WithInactiveStatuses_ReturnsEmpty() {

        LocalDateTime targetExecutionDate = LocalDateTime.of(2026, 4, 4, 0, 0);

        RecurringPlanEntity invalidPlan = TestEntityFactory.createValidRecurringPlan(defaultCategory, defaultWallet);
        invalidPlan.setNextExecutionDate(targetExecutionDate);
        invalidPlan.setStatus(PlanStatus.INACTIVE);
        entityManager.persist(invalidPlan);


        RecurringPlanEntity otherInvalidPlan = TestEntityFactory.createValidRecurringPlan(defaultCategory, defaultWallet);
        otherInvalidPlan.setNextExecutionDate(targetExecutionDate);
        otherInvalidPlan.setStatus(PlanStatus.ENDED);
        entityManager.persist(otherInvalidPlan);

        entityManager.flush();
        entityManager.clear();

        Page<RecurringPlanEntity> result = repository.findPlansToExecute(targetExecutionDate, Pageable.ofSize(15));
        List<RecurringPlanEntity> content = result.getContent();

        assertThat(content).isEmpty();

    }

    @Test
    void findFilteredPlans_WithNullOptionalFilters_ReturnsAllWalletPlans() {

        WalletEntity otherWallet = TestEntityFactory.createValidWallet(defaultUser);
        otherWallet.setName("Other");
        entityManager.persist(otherWallet);


        RecurringPlanEntity validPlan = TestEntityFactory.createValidRecurringPlan(defaultCategory, defaultWallet);
        entityManager.persist(validPlan);

        RecurringPlanEntity invalidPlan = TestEntityFactory.createValidRecurringPlan(defaultCategory, otherWallet);
        entityManager.persist(invalidPlan);

        entityManager.flush();
        entityManager.clear();

        Page<RecurringPlanEntity> result = repository.findFilteredPlans(
                defaultWallet.getId(),
                null,
                null,
                null,
                Pageable.ofSize(15));
        List<RecurringPlanEntity> content = result.getContent();

        assertThat(content)
                .hasSize(1)
                .extracting(RecurringPlanEntity::getId)
                .containsExactly(validPlan.getId());


    }

    @Test
    void findFilteredPlans_WithCategoryFilter_ReturnsOnlyMatchingCategory() {

        CategoryEntity otherCategory = TestEntityFactory.createValidCategory();
        otherCategory.setName("Other");
        entityManager.persist(otherCategory);


        RecurringPlanEntity validPlan = TestEntityFactory.createValidRecurringPlan(defaultCategory, defaultWallet);
        entityManager.persist(validPlan);

        RecurringPlanEntity invalidPlan = TestEntityFactory.createValidRecurringPlan(otherCategory, defaultWallet);
        entityManager.persist(invalidPlan);

        entityManager.flush();
        entityManager.clear();

        Page<RecurringPlanEntity> result = repository.findFilteredPlans(
                defaultWallet.getId(),
                defaultCategory.getId(),
                null,
                null,
                Pageable.ofSize(15)
        );
        List<RecurringPlanEntity> content = result.getContent();

        assertThat(content)
                .hasSize(1)
                .extracting(RecurringPlanEntity::getId)
                .containsExactly(validPlan.getId());

    }

    @Test
    void findFilteredPlans_WithTypeFilter_ReturnsOnlyMatchingType() {

        RecurringPlanEntity validPlan = TestEntityFactory.createValidRecurringPlan(defaultCategory, defaultWallet);
        validPlan.setType(TransactionType.INCOME);
        entityManager.persist(validPlan);

        RecurringPlanEntity invalidPlan = TestEntityFactory.createValidRecurringPlan(defaultCategory, defaultWallet);
        invalidPlan.setType(TransactionType.EXPENSE);
        entityManager.persist(invalidPlan);

        entityManager.flush();
        entityManager.clear();

        Page<RecurringPlanEntity> result = repository.findFilteredPlans(defaultWallet.getId(),
                null,
                TransactionType.INCOME,
                null,
                Pageable.ofSize(15));
        List<RecurringPlanEntity> content = result.getContent();

        assertThat(content)
                .hasSize(1)
                .extracting(RecurringPlanEntity::getId)
                .containsExactly(validPlan.getId());

    }

    @Test
    void findFilteredPlans_WithStatusFilter_ReturnsOnlyMatchingStatus() {

        RecurringPlanEntity validPlan = TestEntityFactory.createValidRecurringPlan(defaultCategory, defaultWallet);
        validPlan.setStatus(PlanStatus.INACTIVE);
        entityManager.persist(validPlan);

        RecurringPlanEntity invalidPlan = TestEntityFactory.createValidRecurringPlan(defaultCategory, defaultWallet);
        invalidPlan.setStatus(PlanStatus.ACTIVE);
        entityManager.persist(invalidPlan);

        RecurringPlanEntity secondInvalidPlan = TestEntityFactory.createValidRecurringPlan(defaultCategory, defaultWallet);
        secondInvalidPlan.setStatus(PlanStatus.ENDED);
        entityManager.persist(secondInvalidPlan);

        entityManager.flush();
        entityManager.clear();

        Page<RecurringPlanEntity> result = repository.findFilteredPlans(defaultWallet.getId(),
                null,
                null,
                PlanStatus.INACTIVE,
                Pageable.ofSize(15));
        List<RecurringPlanEntity> content = result.getContent();

        assertThat(content)
                .hasSize(1)
                .extracting(RecurringPlanEntity::getId)
                .containsExactly(validPlan.getId());
    }


    @Test
    void findFilteredPlans_WithAllFilters_ReturnsExactMatch() {

        RecurringPlanEntity validPlan = TestEntityFactory.createValidRecurringPlan(defaultCategory, defaultWallet);
        validPlan.setStatus(PlanStatus.INACTIVE);
        validPlan.setType(TransactionType.EXPENSE);
        entityManager.persist(validPlan);

        RecurringPlanEntity invalidPlan = TestEntityFactory.createValidRecurringPlan(defaultCategory, defaultWallet);
        invalidPlan.setStatus(PlanStatus.ACTIVE);
        validPlan.setType(TransactionType.EXPENSE);
        entityManager.persist(invalidPlan);

        entityManager.flush();
        entityManager.clear();

        Page<RecurringPlanEntity> result = repository.findFilteredPlans(defaultWallet.getId(),
                defaultCategory.getId(),
                TransactionType.EXPENSE,
                PlanStatus.INACTIVE,
                Pageable.ofSize(15));
        List<RecurringPlanEntity> content = result.getContent();

        assertThat(content)
                .hasSize(1)
                .extracting(RecurringPlanEntity::getId)
                .containsExactly(validPlan.getId());


    }



}