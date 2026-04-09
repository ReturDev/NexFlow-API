package com.returdev.nexflow.controllers;

import com.returdev.nexflow.dto.request.RecurringPlanRequestDTO;
import com.returdev.nexflow.dto.request.update.RecurringPlanUpdateDTO;
import com.returdev.nexflow.dto.response.RecurringPlanResponseDTO;
import com.returdev.nexflow.dto.response.wrapper.ContentWrapperResponseDTO;
import com.returdev.nexflow.dto.response.wrapper.PaginationWrapperResponseDTO;
import com.returdev.nexflow.services.recurring.RecurringPlanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/plans")
@RequiredArgsConstructor
public class RecurringPlanController {

    private final RecurringPlanService recurringPlanService;

    @GetMapping("/{id}")
    public ResponseEntity<ContentWrapperResponseDTO<RecurringPlanResponseDTO>> getRecurringPlanById(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                ContentWrapperResponseDTO.of(
                        recurringPlanService.getRecurringPlanById(id)
                )
        );

    }

    @GetMapping(params = "walletId")
    public ResponseEntity<PaginationWrapperResponseDTO<RecurringPlanResponseDTO>> getWalletRecurringPlans(
            @RequestParam Long walletId,
            Pageable pageable
    ) {

        return ResponseEntity.ok(
                PaginationWrapperResponseDTO.fromPage(
                        recurringPlanService.getRecurringPlansByWalletId(walletId, pageable)
                )
        );

    }


    @GetMapping()
    public ResponseEntity<PaginationWrapperResponseDTO<RecurringPlanResponseDTO>> getRecurringPlans(
            Pageable pageable
    ) {

        return ResponseEntity.ok(
                PaginationWrapperResponseDTO.fromPage(
                        recurringPlanService.getRecurringPlans(pageable)
                )
        );

    }

    @PostMapping()
    public ResponseEntity<ContentWrapperResponseDTO<RecurringPlanResponseDTO>> saveRecurringPlan(
            @RequestBody @Valid RecurringPlanRequestDTO recurringPlanRequestDTO
    ) {

        RecurringPlanResponseDTO response = recurringPlanService.saveRecurringPlan(recurringPlanRequestDTO);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();


        return ResponseEntity.created(location)
                .body(
                        ContentWrapperResponseDTO.of(
                                response
                        )
                );

    }

    @PatchMapping("/{id}")
    public ResponseEntity<ContentWrapperResponseDTO<RecurringPlanResponseDTO>> updateRecurringPlan(
            @PathVariable Long id,
            @RequestBody @Valid RecurringPlanUpdateDTO recurringPlanUpdateDTO
    ) {

        return ResponseEntity.ok(
            ContentWrapperResponseDTO.of(
                    recurringPlanService.updateRecurringPlan(id, recurringPlanUpdateDTO)
            )
        );

    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<ContentWrapperResponseDTO<RecurringPlanResponseDTO>> activatePlan( @PathVariable Long id) {

        return ResponseEntity.ok(
                ContentWrapperResponseDTO.of(recurringPlanService.activatePlan(id))
        );

    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<ContentWrapperResponseDTO<RecurringPlanResponseDTO>> deactivatePlan( @PathVariable Long id) {

        return ResponseEntity.ok(
            ContentWrapperResponseDTO.of(recurringPlanService.deactivatePlan(id))
        );

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlan(@PathVariable Long id) {

        recurringPlanService.deleteWallet(id);

        return ResponseEntity.noContent().build();

    }


}
