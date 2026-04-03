package com.returdev.nexflow.repositories;

import com.returdev.nexflow.model.entities.CategoryEntity;
import com.returdev.nexflow.model.entities.TransactionEntity;
import com.returdev.nexflow.model.entities.UserEntity;
import com.returdev.nexflow.model.entities.WalletEntity;
import com.returdev.nexflow.model.enums.TransactionStatus;
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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TransactionRepositoryTest {

    @Container
    @ServiceConnection
    static MySQLContainer mysql = new MySQLContainer("mysql:8.0");

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TransactionRepository repository;


    private WalletEntity defaultWallet;
    private CategoryEntity defaultCategory;

    @BeforeEach
    void setUp() {
        UserEntity user = entityManager.persist(TestEntityFactory.createValidUser());
        defaultWallet = entityManager.persist(TestEntityFactory.createValidWallet(user));
        defaultCategory = entityManager.persist(TestEntityFactory.createValidCategory());
    }

    @Test
    void findFilteredTransactions_WithNullOptionalFilters_ReturnsAllWalletTransactions() {

        TransactionEntity validTransaction = TestEntityFactory.createValidTransaction(defaultCategory, defaultWallet, null);
        entityManager.persist(validTransaction);

        entityManager.flush();
        entityManager.clear();

        Page<TransactionEntity> page = repository.findFilteredTransactions(defaultWallet.getId(),
                null,
                null,
                null,
                Pageable.ofSize(15));
        List<TransactionEntity> content = page.getContent();


        assertThat(content)
                .hasSize(1)
                .extracting(TransactionEntity::getId)
                .containsExactly(validTransaction.getId());

    }

    @Test
    void findFilteredTransactions_WithCategoryFilter_ReturnsOnlyMatchingCategory() {

        CategoryEntity otherCategory = TestEntityFactory.createValidCategory();
        otherCategory.setName("Other");
        entityManager.persist(otherCategory);

        TransactionEntity validTransaction = TestEntityFactory.createValidTransaction(defaultCategory, defaultWallet, null);
        entityManager.persist(validTransaction);

        TransactionEntity invalidTransaction = TestEntityFactory.createValidTransaction(otherCategory, defaultWallet, null);
        entityManager.persist(invalidTransaction);

        entityManager.flush();
        entityManager.clear();

        Page<TransactionEntity> page = repository.findFilteredTransactions(defaultWallet.getId(),
                defaultCategory.getId(),
                null,
                null,
                Pageable.ofSize(15));
        List<TransactionEntity> content = page.getContent();


        assertThat(content)
                .hasSize(1)
                .extracting(TransactionEntity::getId)
                .containsExactly(validTransaction.getId());

    }

    @Test
    void findFilteredTransactions_WithTypeFilter_ReturnsOnlyMatchingType() {

        TransactionEntity validTransaction = TestEntityFactory.createValidTransaction(defaultCategory, defaultWallet, null);
        validTransaction.setType(TransactionType.INCOME);
        entityManager.persist(validTransaction);

        TransactionEntity invalidTransaction = TestEntityFactory.createValidTransaction(defaultCategory, defaultWallet, null);
        invalidTransaction.setType(TransactionType.EXPENSE);
        entityManager.persist(invalidTransaction);

        entityManager.flush();
        entityManager.clear();

        Page<TransactionEntity> page = repository.findFilteredTransactions(defaultWallet.getId(),
                null,
                TransactionType.INCOME,
                null,
                Pageable.ofSize(15));
        List<TransactionEntity> content = page.getContent();


        assertThat(content)
                .hasSize(1)
                .extracting(TransactionEntity::getId)
                .containsExactly(validTransaction.getId());

    }

    @Test
    void findFilteredTransactions_WithStatusFilter_ReturnsOnlyMatchingStatus() {

        TransactionEntity validTransaction = TestEntityFactory.createValidTransaction(defaultCategory, defaultWallet, null);
        validTransaction.setStatus(TransactionStatus.COMPLETED);
        entityManager.persist(validTransaction);

        TransactionEntity invalidTransaction = TestEntityFactory.createValidTransaction(defaultCategory, defaultWallet, null);
        invalidTransaction.setStatus(TransactionStatus.PENDING);
        entityManager.persist(invalidTransaction);

        entityManager.flush();
        entityManager.clear();

        Page<TransactionEntity> page = repository.findFilteredTransactions(defaultWallet.getId(),
                null,
                null,
                TransactionStatus.COMPLETED,
                Pageable.ofSize(15));
        List<TransactionEntity> content = page.getContent();


        assertThat(content)
                .hasSize(1)
                .extracting(TransactionEntity::getId)
                .containsExactly(validTransaction.getId());

    }

}