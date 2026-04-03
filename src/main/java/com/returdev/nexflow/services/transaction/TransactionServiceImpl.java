package com.returdev.nexflow.services.transaction;

import com.returdev.nexflow.dto.request.TransactionRequestDTO;
import com.returdev.nexflow.dto.request.update.TransactionUpdateDTO;
import com.returdev.nexflow.dto.response.TransactionResponseDTO;
import com.returdev.nexflow.mappers.TransactionMapper;
import com.returdev.nexflow.model.entities.TransactionEntity;
import com.returdev.nexflow.model.enums.TransactionType;
import com.returdev.nexflow.repositories.TransactionRepository;
import com.returdev.nexflow.services.category.CategoryService;
import com.returdev.nexflow.services.wallet.WalletService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * Implementation of {@link TransactionService} that maintains balance integrity.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TransactionServiceImpl implements TransactionService {

    private final CategoryService categoryService;
    private final WalletService walletService;
    private final TransactionRepository repository;
    private final TransactionMapper mapper;

    /**
     * {@inheritDoc}
     */
    @Override
    public TransactionResponseDTO getTransactionById(Long id) {
        return mapper.toResponse(
                findTransactionOrThrow(id)
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<TransactionResponseDTO> getTransactionsByWalletId(Long walletId, Pageable pageable) {
        return repository.findByWalletId(
                walletId,
                pageable
        ).map(mapper::toResponse);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<TransactionResponseDTO> getTransactions(Pageable pageable) {
        return repository.findAll(pageable)
                .map(mapper::toResponse);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public TransactionResponseDTO saveTransaction(TransactionRequestDTO request) {

        categoryService.verifyCategoryExists(request.categoryId());

        applyTransaction(request.walletId(), request.balanceInCents(), request.type());

        return mapper.toResponse(
                repository.save(
                        mapper.toEntity(request)
                )
        );

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveTransactionFromPlan(TransactionEntity transaction) {

        categoryService.verifyCategoryExists(transaction.getCategory().getId());

        applyTransaction(transaction.getWallet().getId(), transaction.getBalanceInCents(), transaction.getType());

        repository.save(transaction);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public TransactionResponseDTO updateTransaction(Long id, TransactionUpdateDTO update) {

        TransactionEntity dbEntity = findTransactionOrThrow(id);
        Long walletId = dbEntity.getWallet().getId();

        if (update.categoryId() != null){
            categoryService.verifyCategoryExists(update.categoryId());
        }

        if (update.balanceInCents() != null || update.type() != null) {

            reverseTransaction(walletId, dbEntity.getBalanceInCents(), dbEntity.getType());

            Long finalBalance = (update.balanceInCents() != null) ? update.balanceInCents() : dbEntity.getBalanceInCents();
            TransactionType finalType = (update.type() != null) ? update.type() : dbEntity.getType();

            applyTransaction(walletId, finalBalance, finalType);
        }

        mapper.updateEntity(update, dbEntity);

        return mapper.toResponse(
                repository.save(dbEntity)
        );

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteTransaction(Long id) {
        TransactionEntity entity = findTransactionOrThrow(id);

        reverseTransaction(entity.getWallet().getId(), entity.getBalanceInCents(), entity.getType());

        repository.delete(entity);
    }

    /**
     * Helper to retrieve a transaction or throw localized exception.
     */
    private TransactionEntity findTransactionOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("exception.transaction.not_found"));
    }

    /**
     * Reverses the financial impact of a transaction.
     * Incomes are subtracted, Expenses are added back.
     */
    private void reverseTransaction(Long walletId, Long balance, TransactionType type) {
        switch (type) {
            case INCOME -> walletService.decrementWalletBalance(walletId, balance);
            case EXPENSE -> walletService.incrementWalletBalance(walletId, balance);
        }
    }

    /**
     * Applies the financial impact of a transaction.
     * Incomes are added, Expenses are subtracted.
     */
    private void applyTransaction(Long walletId, Long balance, TransactionType type) {
        switch (type) {
            case INCOME -> walletService.incrementWalletBalance(walletId, balance);
            case EXPENSE -> walletService.decrementWalletBalance(walletId, balance);
        }
    }

}
