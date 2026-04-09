package com.returdev.nexflow.controllers;

import com.returdev.nexflow.dto.request.TransactionRequestDTO;
import com.returdev.nexflow.dto.request.update.TransactionUpdateDTO;
import com.returdev.nexflow.dto.response.TransactionResponseDTO;
import com.returdev.nexflow.dto.response.wrapper.ContentWrapperResponseDTO;
import com.returdev.nexflow.dto.response.wrapper.PaginationWrapperResponseDTO;
import com.returdev.nexflow.services.transaction.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/transaction")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;


    @GetMapping("/{id}")
    public ResponseEntity<ContentWrapperResponseDTO<TransactionResponseDTO>> getTransactionById(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                ContentWrapperResponseDTO.of(
                        transactionService.getTransactionById(id)
                )
        );

    }

    @GetMapping(params = "walletId")
    public ResponseEntity<PaginationWrapperResponseDTO<TransactionResponseDTO>> getWalletTransactions(
            @RequestParam() Long walletId,
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                PaginationWrapperResponseDTO.fromPage(
                        transactionService.getTransactionsByWalletId(walletId, pageable)
                )
        );
    }

    @GetMapping()
    public ResponseEntity<PaginationWrapperResponseDTO<TransactionResponseDTO>> getTransactions(
            Pageable pageable
    ) {

        return ResponseEntity.ok(
                PaginationWrapperResponseDTO.fromPage(
                        transactionService.getTransactions(pageable)
                )
        );

    }

    @PostMapping()
    public ResponseEntity<ContentWrapperResponseDTO<TransactionResponseDTO>> saveTransaction(
            @RequestBody TransactionRequestDTO transactionRequestDTO
    ) {

        TransactionResponseDTO response = transactionService.saveTransaction(transactionRequestDTO);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();

        return ResponseEntity.created(location)
                .body(
                        ContentWrapperResponseDTO.of(response)
                );

    }

    @PatchMapping("/{id}")
    public ResponseEntity<ContentWrapperResponseDTO<TransactionResponseDTO>> updateTransaction(
            @PathVariable Long id,
            @RequestBody TransactionUpdateDTO transactionUpdateDTO
    ) {

        return ResponseEntity.ok(
                ContentWrapperResponseDTO.of(transactionService.updateTransaction(id, transactionUpdateDTO))
        );

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(
            @PathVariable Long id
    ) {

        transactionService.deleteTransaction(id);

        return ResponseEntity.noContent().build();
    }

}
