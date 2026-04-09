package com.returdev.nexflow.controllers;

import com.returdev.nexflow.dto.request.WalletRequestDTO;
import com.returdev.nexflow.dto.request.update.WalletUpdateDTO;
import com.returdev.nexflow.dto.response.WalletResponseDTO;
import com.returdev.nexflow.dto.response.wrapper.ContentWrapperResponseDTO;
import com.returdev.nexflow.dto.response.wrapper.PaginationWrapperResponseDTO;
import com.returdev.nexflow.services.wallet.WalletService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/wallets")
public class WalletController {

    private final WalletService walletService;


    @GetMapping("/{id}")
    public ResponseEntity<ContentWrapperResponseDTO<WalletResponseDTO>> getWalletById(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                ContentWrapperResponseDTO.of(walletService.getWalletById(id))
        );

    }

    @GetMapping(params = "userId")
    public ResponseEntity<PaginationWrapperResponseDTO<WalletResponseDTO>> getWalletsByUserId(
            @RequestParam() UUID userId,
            Pageable pageable
    ) {

        return ResponseEntity.ok(
                PaginationWrapperResponseDTO.fromPage(
                        walletService.getWalletsOfUser(userId, pageable)
                )
        );

    }

    @GetMapping()
    public ResponseEntity<PaginationWrapperResponseDTO<WalletResponseDTO>> getWallets(
            @RequestParam(required = false) UUID userId,
            Pageable pageable
    ) {

        return ResponseEntity.ok(
                PaginationWrapperResponseDTO.fromPage(
                        walletService.getWallets(pageable)
                )
        );

    }

    @PostMapping()
    public ResponseEntity<ContentWrapperResponseDTO<WalletResponseDTO>> saveWallet(
            @RequestBody @Valid WalletRequestDTO walletRequestDTO
    ) {

        WalletResponseDTO response = walletService.saveWallet(walletRequestDTO);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();
        ;

        return ResponseEntity.created(location)
                .body(
                        ContentWrapperResponseDTO.of(response)
                );

    }

    @PatchMapping("/{id}")
    public ResponseEntity<ContentWrapperResponseDTO<WalletResponseDTO>> updateWallet(
            @PathVariable Long id,
            @RequestBody @Valid WalletUpdateDTO walletUpdateDTO
    ) {

        return ResponseEntity.ok(
                ContentWrapperResponseDTO.of(walletService.updateWallet(id, walletUpdateDTO))
        );


    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWallet(
            @PathVariable Long id
    ) {

        walletService.deleteWallet(id);

        return ResponseEntity.noContent().build();

    }


}
