package com.returdev.nexflow.services.transaction;

import com.returdev.nexflow.dto.request.TransactionRequestDTO;
import com.returdev.nexflow.dto.request.update.TransactionUpdateDTO;
import com.returdev.nexflow.dto.response.TransactionResponseDTO;
import com.returdev.nexflow.mappers.TransactionMapper;
import com.returdev.nexflow.model.entities.TransactionEntity;
import com.returdev.nexflow.model.entities.UserEntity;
import com.returdev.nexflow.model.enums.Role;
import com.returdev.nexflow.model.enums.TransactionType;
import com.returdev.nexflow.model.exceptions.OverdraftLimitException;
import com.returdev.nexflow.model.exceptions.ResourceNotFoundException;
import com.returdev.nexflow.model.facade.AuthenticationFacade;
import com.returdev.nexflow.repositories.TransactionRepository;
import com.returdev.nexflow.services.category.CategoryService;
import com.returdev.nexflow.services.wallet.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;


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

    private final AuthenticationFacade authenticationFacade;

    /**
     * {@inheritDoc}
     *
     * @throws ResourceNotFoundException if the transaction does not exist or access is denied.
     */
    @Override
    public TransactionResponseDTO getTransactionById(Long id) {
        return mapper.toResponse(
                findByIdWithVerification(id)
        );
    }

    /**
     * {@inheritDoc}
     *
     * @throws ResourceNotFoundException if the requester is not an ADMIN and does not own the wallet.
     */
    @Override
    public Page<TransactionResponseDTO> getTransactionsByWalletId(Long walletId, Pageable pageable) {

        final Page<TransactionEntity> page;

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
    public Page<TransactionResponseDTO> getTransactions(Pageable pageable) {
        return repository.findAll(pageable)
                .map(mapper::toResponse);
    }

    /**
     * {@inheritDoc}
     *
     * @throws ResourceNotFoundException if the wallet or category is not found.
     * @throws OverdraftLimitException   if an EXPENSE exceeds the wallet's allowed overdraft.
     */
    @Override
    @Transactional
    public TransactionResponseDTO saveTransaction(TransactionRequestDTO request) {

        UserEntity authUser = authenticationFacade.getAuthenticateUser();

        if (authUser.getRole() != Role.ADMIN) {
            walletService.verifyExistsWalletOfUser(request.walletId(), authUser.getId());
        }

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
     *
     * @throws OverdraftLimitException   if the transaction violates wallet constraints.
     * @throws ResourceNotFoundException if the associated category is missing.
     * @apiNote This method does not perform ownership or security verification.
     * Used by automated processes (like recurring plans).
     */
    @Override
    public void saveTransactionFromPlan(TransactionEntity transaction) {

        categoryService.verifyCategoryExists(transaction.getCategory().getId());

        applyTransaction(transaction.getWallet().getId(), transaction.getBalanceInCents(), transaction.getType());

        repository.save(transaction);

    }

    /**
     * {@inheritDoc}
     *
     * @throws ResourceNotFoundException if the transaction or new category is not found.
     * @throws OverdraftLimitException   if the update results in an illegal negative balance.
     */
    @Override
    @Transactional
    public TransactionResponseDTO updateTransaction(Long id, TransactionUpdateDTO update) {

        TransactionEntity dbTransaction = findByIdWithVerification(id);

        if (update.categoryId() != null) {
            categoryService.verifyCategoryExists(update.categoryId());
        }

        Long walletId = dbTransaction.getWallet().getId();

        if (update.balanceInCents() != null || update.type() != null) {

            reverseTransaction(walletId, dbTransaction.getBalanceInCents(), dbTransaction.getType());

            Long finalBalance = (update.balanceInCents() != null) ? update.balanceInCents() : dbTransaction.getBalanceInCents();
            TransactionType finalType = (update.type() != null) ? update.type() : dbTransaction.getType();

            applyTransaction(walletId, finalBalance, finalType);
        }

        mapper.updateEntity(update, dbTransaction);

        return mapper.toResponse(
                repository.save(dbTransaction)
        );

    }

    /**
     * {@inheritDoc}
     *
     * @throws ResourceNotFoundException if the transaction is not found or access is denied.
     */
    @Override
    public void deleteTransaction(Long id) {
        TransactionEntity transaction = findByIdWithVerification(id);

        reverseTransaction(transaction.getWallet().getId(), transaction.getBalanceInCents(), transaction.getType());

        repository.delete(transaction);
    }

    /**
     * Validates access based on security context. Admins bypass ownership checks.
     *
     * @throws ResourceNotFoundException if the entity is not found or user is unauthorized.
     */
    private TransactionEntity findByIdWithVerification(Long transactionId) {
        UserEntity authUser = authenticationFacade.getAuthenticateUser();

        if (authUser.getRole() == Role.ADMIN) {
            return findTransactionOrThrow(transactionId);
        } else {
            return findTransactionOfUserOrThrow(transactionId, authUser.getId());
        }

    }

    /**
     * Retrieves a transaction entity by its primary key.
     *
     * @param id the unique identifier of the transaction.
     * @return the {@link TransactionEntity} if found.
     * @throws ResourceNotFoundException if no transaction exists with the given ID.
     */
    private TransactionEntity findTransactionOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("exception.transaction.not_found"));
    }

    /**
     * Retrieves a transaction while strictly validating ownership.
     *
     * @param transactionId the unique identifier of the transaction.
     * @param userId        the UUID of the user to verify ownership against.
     * @return the {@link TransactionEntity} if found and authorized.
     * @throws ResourceNotFoundException if the transaction is missing or belongs to another user.
     */
    private TransactionEntity findTransactionOfUserOrThrow(Long transactionId, UUID userId) {
        return repository.findByIdAndWalletUserId(transactionId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("exception.transaction.not_found"));
    }

    /**
     * Reverses the financial impact of a previously processed transaction on a wallet.
     *
     * @param walletId the identifier of the wallet to adjust.
     * @param balance  the amount in cents to reverse.
     * @param type     the {@link TransactionType} of the original transaction.
     */
    private void reverseTransaction(Long walletId, Long balance, TransactionType type) {
        switch (type) {
            case INCOME -> walletService.decrementWalletBalance(walletId, balance);
            case EXPENSE -> walletService.incrementWalletBalance(walletId, balance);
        }
    }

    /**
     * Applies a new financial impact to a wallet balance.
     *
     * @param walletId the identifier of the wallet to adjust.
     * @param balance  the amount in cents to apply.
     * @param type     the {@link TransactionType} of the transaction.
     */
    private void applyTransaction(Long walletId, Long balance, TransactionType type) {
        switch (type) {
            case INCOME -> walletService.incrementWalletBalance(walletId, balance);
            case EXPENSE -> walletService.decrementWalletBalance(walletId, balance);
        }
    }

}
