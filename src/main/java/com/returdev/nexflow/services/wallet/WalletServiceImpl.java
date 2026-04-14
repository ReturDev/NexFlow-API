package com.returdev.nexflow.services.wallet;

import com.returdev.nexflow.dto.request.WalletRequestDTO;
import com.returdev.nexflow.dto.request.update.WalletUpdateDTO;
import com.returdev.nexflow.dto.response.WalletResponseDTO;
import com.returdev.nexflow.mappers.WalletMapper;
import com.returdev.nexflow.model.entities.UserEntity;
import com.returdev.nexflow.model.entities.WalletEntity;
import com.returdev.nexflow.model.enums.Role;
import com.returdev.nexflow.model.exceptions.FieldAlreadyExistException;
import com.returdev.nexflow.model.exceptions.MaxWalletsReachedException;
import com.returdev.nexflow.model.exceptions.OverdraftLimitException;
import com.returdev.nexflow.model.exceptions.ResourceNotFoundException;
import com.returdev.nexflow.model.facade.AuthenticationFacade;
import com.returdev.nexflow.repositories.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;


/**
 * Implementation of {@link WalletService} using Spring Data JPA.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WalletServiceImpl implements WalletService {


    private static final int MAX_WALLETS_BY_USER = 5;
    private final WalletRepository repository;
    private final WalletMapper mapper;

    private final AuthenticationFacade authenticationFacade;

    /**
     * {@inheritDoc}
     *
     * @throws ResourceNotFoundException if the user is not authorized to view these wallets.
     */
    @Override
    public Page<WalletResponseDTO> getWalletsOfUser(UUID userId, Pageable pageable) {

        UserEntity authUser = authenticationFacade.getAuthenticateUser();

        if (authUser.getRole() != Role.ADMIN && !authUser.getId().equals(userId)) {
            throw new ResourceNotFoundException("exception.wallet.not_found");
        }

        return repository.findAllByUserId(userId, pageable).map(mapper::toResponse);
    }

    /**
     * {@inheritDoc}
     *
     * @throws ResourceNotFoundException if the user is not authorized to view these wallets
     *                                   or the wallet does not exist.
     */
    @Override
    public WalletResponseDTO getWalletById(Long id) {
        return mapper.toResponse(
                findByIdWithVerification(id)
        );
    }

    /**
     * {@inheritDoc}
     *
     * @apiNote This method does not perform ownership or security verification.
     * Access control (e.g., ADMIN role check) must be managed at the controller
     * or via security expressions.
     */
    @Override
    public Page<WalletResponseDTO> getWallets(Pageable pageable) {
        return repository.findAll(pageable)
                .map(mapper::toResponse);
    }

    /**
     * {@inheritDoc}
     *
     * @throws ResourceNotFoundException  if the user is not authorized to view these wallets.
     * @throws FieldAlreadyExistException if the wallet name is taken.
     * @throws MaxWalletsReachedException if the user exceeds the creation limit.
     */
    @Override
    @Transactional
    public WalletResponseDTO saveWallet(WalletRequestDTO wallet) {

        UserEntity authUser = authenticationFacade.getAuthenticateUser();

        if (authUser.getRole() != Role.ADMIN && !wallet.userId().equals(authUser.getId())) {
            throw new ResourceNotFoundException("exception.user.not_found");
        }

        if (repository.existsByName(wallet.name())) {
            throw new FieldAlreadyExistException("exception.wallet.name_already_exists", wallet.name());
        }

        if (repository.countByUserId(wallet.userId()) >= MAX_WALLETS_BY_USER) {
            throw new MaxWalletsReachedException(MAX_WALLETS_BY_USER);
        }

        return mapper.toResponse(
                repository.save(
                        mapper.toEntity(wallet)
                )
        );
    }

    /**
     * {@inheritDoc}
     *
     * @throws ResourceNotFoundException if the wallet does not exist
     *                                   or the user is not authorized to view these wallets.
     */
    @Override
    @Transactional
    public WalletResponseDTO updateWallet(Long walletId, WalletUpdateDTO wallet) {
        WalletEntity dbWallet = findByIdWithVerification(walletId);

        mapper.updateEntity(wallet, dbWallet);

        return mapper.toResponse(
                repository.save(dbWallet)
        );

    }

    /**
     * {@inheritDoc}
     *
     * @throws ResourceNotFoundException if the wallet does not exist.
     * @apiNote This method does not perform ownership or authorization checks.
     * Security verification must be handled by the caller or the orchestrating service
     * before invoking this method.
     */
    @Override
    @Transactional
    public void incrementWalletBalance(Long walletId, Long balanceToIncrement) {

        WalletEntity dbWallet = findWalletOrThrow(walletId);

        Long updatedBalance = dbWallet.getBalanceInCents() + balanceToIncrement;

        dbWallet.setBalanceInCents(updatedBalance);

        repository.save(dbWallet);

    }

    /**
     * {@inheritDoc}
     *
     * @throws ResourceNotFoundException if the wallet does not exist.
     * @throws OverdraftLimitException   if the resulting balance would exceed the overdraft limit.
     * @apiNote This method does not perform ownership or authorization checks.
     * Security verification must be handled by the caller or the orchestrating service
     * before invoking this method.
     */
    @Override
    public void decrementWalletBalance(Long walletId, Long balanceToDecrement) {

        WalletEntity dbEntity = findWalletOrThrow(walletId);

        long updatedBalance = dbEntity.getBalanceInCents() - balanceToDecrement;

        if (updatedBalance < -dbEntity.getOverdraftLimit()) {
            throw new OverdraftLimitException(updatedBalance, dbEntity.getOverdraftLimit());
        }

        dbEntity.setBalanceInCents(updatedBalance);

        repository.save(dbEntity);

    }

    /**
     * {@inheritDoc}
     *
     * @throws ResourceNotFoundException if the wallet does not exist
     *                                   or the user is not authorized to view these wallets.
     */
    @Override
    @Transactional
    public void deleteWallet(Long id) {
        WalletEntity wallet = findByIdWithVerification(id);
        repository.delete(wallet);
    }

    /**
     * {@inheritDoc}
     *
     * @throws ResourceNotFoundException if the wallet does not exist.
     */
    @Override
    public void verifyExistsWalletOfUser(Long walletId, UUID userid) {
        if (!repository.existsByIdAndUserId(walletId, userid)) {
            throw new ResourceNotFoundException("exception.wallet.not_found");
        }
    }

    /**
     * Internal utility to find a wallet by ID or throw a 404 error.
     *
     * @param id the wallet ID.
     * @return the {@link WalletEntity}.
     * @throws ResourceNotFoundException if the wallet is missing.
     */
    private WalletEntity findWalletOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("exception.wallet.not_found"));
    }

    /**
     * Internal utility to find a wallet owned by a specific user.
     *
     * @param walletId the wallet ID.
     * @param userId   the owner's UUID.
     * @return the {@link WalletEntity}.
     * @throws ResourceNotFoundException if the wallet is missing or access is denied.
     */
    private WalletEntity findWalletOfUserOrThrow(Long walletId, UUID userId) {
        return repository.findByIdAndUserId(walletId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("exception.wallet.not_found"));
    }

    /**
     * Fetches a wallet while applying multi-tenant security logic.
     *
     * @param walletId the ID of the wallet to retrieve.
     * @return the verified {@link WalletEntity}.
     */
    private WalletEntity findByIdWithVerification(Long walletId) {
        UserEntity authUser = authenticationFacade.getAuthenticateUser();

        if (authUser.getRole() == Role.ADMIN) {
            return findWalletOrThrow(walletId);
        } else {
            return findWalletOfUserOrThrow(walletId, authUser.getId());
        }
    }

}


