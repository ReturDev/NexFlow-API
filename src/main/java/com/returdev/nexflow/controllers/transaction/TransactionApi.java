package com.returdev.nexflow.controllers.transaction;

import com.returdev.nexflow.annotations.swagger.*;
import com.returdev.nexflow.dto.request.TransactionRequestDTO;
import com.returdev.nexflow.dto.request.update.TransactionUpdateDTO;
import com.returdev.nexflow.dto.response.TransactionResponseDTO;
import com.returdev.nexflow.dto.response.wrapper.ContentWrapperResponseDTO;
import com.returdev.nexflow.dto.response.wrapper.PaginationWrapperResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Transactions", description = "Endpoints for managing financial transactions")
@SecurityRequirement(name = "bearerAuth")
@InternalServerErrorResponseCode
public interface TransactionApi {

    @Operation(
            summary = "Get transaction by ID",
            description = "Retrieves the details of a specific transaction using its unique identifier."
    )
    @OkResponseCode
    @NotFoundResponseCode
    ResponseEntity<ContentWrapperResponseDTO<TransactionResponseDTO>> getTransactionById(
            @Parameter(description = "The unique ID of the transaction", required = true) Long id
    );

    @Operation(
            summary = "Get transactions by Wallet",
            description = "Retrieves a paginated list of transactions associated with a specific wallet."
    )
    @OkResponseCode
    ResponseEntity<PaginationWrapperResponseDTO<TransactionResponseDTO>> getWalletTransactions(
            @Parameter(description = "The unique ID of the wallet", required = true) Long walletId,
            @Parameter(description = "Pagination parameters (page, size, sort)") Pageable pageable
    );

    @Operation(
            summary = "Get all user transactions",
            description = "Retrieves a paginated list of all transactions belonging to the authenticated user."
    )
    @OkResponseCode
    ResponseEntity<PaginationWrapperResponseDTO<TransactionResponseDTO>> getTransactions(
            @Parameter(description = "Pagination parameters (page, size, sort)") Pageable pageable
    );

    @Operation(
            summary = "Create a new transaction",
            description = "Registers a new income or expense transaction in the system."
    )
    @CreatedResponseCode
    @BadRequestResponseCode
    ResponseEntity<ContentWrapperResponseDTO<TransactionResponseDTO>> saveTransaction(
            @Valid @RequestBody TransactionRequestDTO transactionRequestDTO
    );

    @Operation(
            summary = "Update an existing transaction",
            description = "Updates the information of a transaction. Only provided fields will be modified."
    )
    @OkResponseCode
    @BadRequestResponseCode
    @NotFoundResponseCode
    ResponseEntity<ContentWrapperResponseDTO<TransactionResponseDTO>> updateTransaction(
            @Parameter(description = "The unique ID of the transaction", required = true) Long id,
            TransactionUpdateDTO transactionUpdateDTO
    );

    @Operation(
            summary = "Delete a transaction",
            description = "Permanently removes a transaction from the system."
    )
    @NoContentResponseCode
    @NotFoundResponseCode
    ResponseEntity<Void> deleteTransaction(
            @Parameter(description = "The unique ID of the transaction", required = true) Long id
    );
}
