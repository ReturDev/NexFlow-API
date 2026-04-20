package com.returdev.nexflow.controllers.wallet;

import com.returdev.nexflow.annotations.swagger.*;
import com.returdev.nexflow.dto.request.WalletRequestDTO;
import com.returdev.nexflow.dto.request.update.WalletUpdateDTO;
import com.returdev.nexflow.dto.response.WalletResponseDTO;
import com.returdev.nexflow.dto.response.wrapper.ContentWrapperResponseDTO;
import com.returdev.nexflow.dto.response.wrapper.PaginationWrapperResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

@Tag(name = "Wallets", description = "Endpoints for managing digital wallets and balances")
@SecurityRequirement(name = "bearerAuth")
@UnauthorizedResponseCode
@ForbiddenResponseCode
@InternalServerErrorResponseCode
public interface WalletApi {

    @Operation(
            summary = "Get wallet by ID",
            description = "Retrieves the details, currency, and current balance of a specific wallet."
    )
    @OkResponseCode
    @NotFoundResponseCode
    ResponseEntity<ContentWrapperResponseDTO<WalletResponseDTO>> getWalletById(
            @Parameter(description = "The unique ID of the wallet", required = true) Long id
    );

    @Operation(
            summary = "Get wallets by User ID",
            description = "Retrieves a paginated list of all wallets belonging to a specific user UUID."
    )
    @OkResponseCode
    ResponseEntity<PaginationWrapperResponseDTO<WalletResponseDTO>> getWalletsByUserId(
            @Parameter(description = "The UUID of the owner user", required = true) UUID userId,
            @Parameter(description = "Pagination parameters (page, size, sort)") Pageable pageable
    );

    @Operation(
            summary = "Get all wallets (Global)",
            description = "Retrieves a paginated list of wallets. Use this for administrative or general overview purposes."
    )
    @OkResponseCode
    ResponseEntity<PaginationWrapperResponseDTO<WalletResponseDTO>> getWallets(
            @Parameter(description = "The UUID of the user to filter (optional)", required = false) UUID userId,
            @Parameter(description = "Pagination parameters (page, size, sort)") Pageable pageable
    );

    @Operation(
            summary = "Create a new wallet",
            description = "Registers a new wallet for the authenticated user with a specific currency."
    )
    @CreatedResponseCode
    @BadRequestResponseCode
    @ConflictResponseCode
    ResponseEntity<ContentWrapperResponseDTO<WalletResponseDTO>> saveWallet(
            WalletRequestDTO walletRequestDTO
    );

    @Operation(
            summary = "Update wallet details",
            description = "Updates the name or settings of an existing wallet."
    )
    @OkResponseCode
    @BadRequestResponseCode
    @NotFoundResponseCode
    ResponseEntity<ContentWrapperResponseDTO<WalletResponseDTO>> updateWallet(
            @Parameter(description = "The unique ID of the wallet", required = true) Long id,
            WalletUpdateDTO walletUpdateDTO
    );

    @Operation(
            summary = "Delete wallet",
            description = "Removes a wallet from the system. Note: A wallet cannot be deleted if it contains transactions (Conflict)."
    )
    @NoContentResponseCode
    @NotFoundResponseCode
    @ConflictResponseCode
    ResponseEntity<Void> deleteWallet(
            @Parameter(description = "The unique ID of the wallet to delete", required = true) Long id
    );

}
