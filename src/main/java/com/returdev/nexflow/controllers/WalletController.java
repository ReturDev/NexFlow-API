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
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/wallet")
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

    @GetMapping("/user/{id}")
    public ResponseEntity<ContentWrapperResponseDTO<List<WalletResponseDTO>>> getWalletsOfUser(
            @PathVariable("id") UUID userId
    ) {

        return ResponseEntity.ok(
                ContentWrapperResponseDTO.of(
                        walletService.getWalletsOfUser(userId)
                )
        );

    }

    @GetMapping()
    public ResponseEntity<PaginationWrapperResponseDTO<WalletResponseDTO>> getWallets(
            @Valid Pageable pageable
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
