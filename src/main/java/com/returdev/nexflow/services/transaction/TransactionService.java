package com.returdev.nexflow.services.transaction;

import com.returdev.nexflow.dto.request.TransactionRequestDTO;
import com.returdev.nexflow.dto.request.update.TransactionUpdateDTO;
import com.returdev.nexflow.dto.response.TransactionResponseDTO;
import com.returdev.nexflow.model.entities.TransactionEntity;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;

/**
 * Service interface for managing financial transactions.
 */
@Validated
public interface TransactionService {

    /**
     * Retrieves a single transaction by its unique ID.
     *
     * @param id the transaction identifier.
     * @return the found {@link TransactionResponseDTO}.
     * @throws EntityNotFoundException if the transaction does not exist.
     */
    TransactionResponseDTO getTransactionById(Long id);

    /**
     * Retrieves a paginated list of transactions for a specific wallet.
     *
     * @param walletId the ID of the wallet to filter by.
     * @param pageable pagination and sorting parameters.
     * @return a {@link Page} of {@link TransactionResponseDTO}.
     */
    Page<TransactionResponseDTO> getTransactionsByWalletId(Long walletId, Pageable pageable);

    /**
     * Returns a paginated list of all transactions across the system.
     *
     * @param pageable pagination parameters.
     * @return a {@link Page} of {@link TransactionResponseDTO}.
     */
    Page<TransactionResponseDTO> getTransactions(Pageable pageable);

    /**
     * Creates and executes a new transaction.
     *
     * @param request the transaction details.
     * @return the persisted {@link TransactionResponseDTO}.
     */
    TransactionResponseDTO saveTransaction(@Valid TransactionRequestDTO request);

    /**
     * Persists a transaction generated automatically by a recurring plan.
     *
     * @param transaction the fully prepared {@link TransactionEntity} ready for persistence.
     */
    void saveTransactionFromPlan(@Valid TransactionEntity transaction);

    /**
     * Partially updates an existing transaction and adjusts the wallet balance
     * accordingly if the amount or type has changed.
     *
     * @param id     the ID of the transaction to update.
     * @param update the update data.
     * @return the modified {@link TransactionResponseDTO}.
     */
    TransactionResponseDTO updateTransaction(Long id, @Valid TransactionUpdateDTO update);

    /**
     * Deletes a transaction and reverses its impact on the associated wallet balance.
     *
     * @param id the ID of the transaction to remove.
     */
    void deleteTransaction(Long id);
}
