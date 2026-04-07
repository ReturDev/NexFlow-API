package com.returdev.nexflow.services.wallet;

import com.returdev.nexflow.dto.request.WalletRequestDTO;
import com.returdev.nexflow.dto.request.update.WalletUpdateDTO;
import com.returdev.nexflow.dto.response.WalletResponseDTO;
import com.returdev.nexflow.mappers.WalletMapper;
import com.returdev.nexflow.model.entities.WalletEntity;
import com.returdev.nexflow.model.exceptions.FieldAlreadyExistException;
import com.returdev.nexflow.model.exceptions.MaxWalletsReachedException;
import com.returdev.nexflow.model.exceptions.OverdraftLimitException;
import com.returdev.nexflow.model.exceptions.ResourceNotFoundException;
import com.returdev.nexflow.repositories.WalletRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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

    /**
     * {@inheritDoc}
     */
    @Override
    public List<WalletResponseDTO> getWalletsOfUser(UUID userId) {
        return repository.findAllByUserId(userId).stream().map(mapper::toResponse).toList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WalletResponseDTO getWalletById(Long id) {
        return mapper.toResponse(
                findWalletOrThrow(id)
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<WalletResponseDTO> getWallets(Pageable pageable) {
        return repository.findAll(pageable)
                .map(mapper::toResponse);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public WalletResponseDTO saveWallet(WalletRequestDTO wallet) {

        if (repository.existsByName(wallet.name())){
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
     */
    @Override
    @Transactional
    public WalletResponseDTO updateWallet(Long walletId, WalletUpdateDTO wallet) {
        WalletEntity dbWallet = findWalletOrThrow(walletId);

        mapper.updateEntity(wallet, dbWallet);

        return mapper.toResponse(
                repository.save(dbWallet)
        );

    }

    /**
     * {@inheritDoc}
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
     */
    @Override
    @Transactional
    public void deleteWallet(Long id) {
        WalletEntity wallet = findWalletOrThrow(id);
        repository.delete(wallet);
    }

    /**
     * Internal utility to find a wallet or throw a localized exception.
     *
     * @param id the wallet ID.
     * @return the {@link WalletEntity} if found.
     * @throws EntityNotFoundException if the wallet is missing.
     */
    private WalletEntity findWalletOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("exception.wallet.not_found"));
    }

}


