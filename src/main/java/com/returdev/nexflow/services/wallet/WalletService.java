package com.returdev.nexflow.services.wallet;

import com.returdev.nexflow.dto.request.WalletRequestDTO;
import com.returdev.nexflow.dto.request.update.WalletUpdateDTO;
import com.returdev.nexflow.dto.response.WalletResponseDTO;
import com.returdev.nexflow.model.exceptions.BusinessException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for managing user wallets and financial balances.
 */
@Validated
public interface WalletService {

    /**
     * Retrieves all wallets associated with a specific user.
     *
     * @param userId the unique identifier of the owner.
     * @return a list of {@link WalletResponseDTO} belonging to the user.
     */
    List<WalletResponseDTO> getWalletsOfUser(UUID userId);

    /**
     * Retrieves a single wallet by its ID.
     *
     * @param id the wallet's unique identifier.
     * @return the found {@link WalletResponseDTO}.
     * @throws EntityNotFoundException if the wallet does not exist.
     */
    WalletResponseDTO getWalletById(Long id);

    /**
     * Retrieves a paginated list of all wallets in the system.
     *
     * @param pageable the pagination and sorting parameters (e.g., page number,
     * size, and sort direction).
     * @return a {@link org.springframework.data.domain.Page} containing
     * {@link WalletResponseDTO} objects and metadata about the total result set.
     */
    Page<WalletResponseDTO> getWallets(Pageable pageable);

    /**
     * Creates a new wallet for a user, enforcing system-wide limits.
     *
     * @param wallet the registration data for the new wallet.
     * @return the created {@link WalletResponseDTO}.
     * @throws BusinessException if the user has reached the maximum wallet limit.
     */
    WalletResponseDTO saveWallet(@Valid WalletRequestDTO wallet);

    /**
     * Partially updates an existing wallet's configuration.
     *
     * @param walletId the ID of the wallet to update.
     * @param wallet   the update data.
     * @return the modified {@link WalletResponseDTO}.
     */
    WalletResponseDTO updateWallet(Long walletId, @Valid WalletUpdateDTO wallet);

    /**
     * Atomically increases the wallet's balance.
     *
     * @param walletId           the ID of the wallet.
     * @param balanceToIncrement the amount in cents to add.
     */
    void incrementWalletBalance(Long walletId, Long balanceToIncrement);

    /**
     * Atomically decreases the wallet's balance, checking against the overdraft limit.
     *
     * @param walletId           the ID of the wallet.
     * @param balanceToDecrement the amount in cents to subtract.
     * @throws BusinessException if the resulting balance would exceed the overdraft limit.
     */
    void decrementWalletBalance(Long walletId, Long balanceToDecrement);

    /**
     * Removes a wallet from the system.
     *
     * @param id the ID of the wallet to delete.
     */
    void deleteWallet(Long id);
}

