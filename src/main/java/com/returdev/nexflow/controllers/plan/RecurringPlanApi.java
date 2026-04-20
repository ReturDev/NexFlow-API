package com.returdev.nexflow.controllers.plan;

import com.returdev.nexflow.annotations.swagger.*;
import com.returdev.nexflow.dto.request.RecurringPlanRequestDTO;
import com.returdev.nexflow.dto.request.update.RecurringPlanUpdateDTO;
import com.returdev.nexflow.dto.response.RecurringPlanResponseDTO;
import com.returdev.nexflow.dto.response.wrapper.ContentWrapperResponseDTO;
import com.returdev.nexflow.dto.response.wrapper.PaginationWrapperResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Recurring Plans", description = "Endpoints for managing recurring financial plans")
@SecurityRequirement(name = "bearerAuth")
@InternalServerErrorResponseCode
public interface RecurringPlanApi {

    @Operation(summary = "Get plan by ID")
    @OkResponseCode
    @NotFoundResponseCode
    ResponseEntity<ContentWrapperResponseDTO<RecurringPlanResponseDTO>> getRecurringPlanById(
            @Parameter(description = "Plan ID", required = true) Long id
    );

    @Operation(summary = "Get all plans of a wallet")
    @OkResponseCode
    ResponseEntity<PaginationWrapperResponseDTO<RecurringPlanResponseDTO>> getWalletRecurringPlans(
            @Parameter(description = "Wallet ID", required = true) Long walletId,
            Pageable pageable
    );

    @Operation(summary = "Get all user plans")
    @OkResponseCode
    ResponseEntity<PaginationWrapperResponseDTO<RecurringPlanResponseDTO>> getRecurringPlans(Pageable pageable);

    @Operation(summary = "Save new plan")
    @CreatedResponseCode
    @BadRequestResponseCode
    ResponseEntity<ContentWrapperResponseDTO<RecurringPlanResponseDTO>> saveRecurringPlan(
            RecurringPlanRequestDTO recurringPlanRequestDTO
    );

    @Operation(summary = "Update plan")
    @OkResponseCode
    @BadRequestResponseCode
    @NotFoundResponseCode
    ResponseEntity<ContentWrapperResponseDTO<RecurringPlanResponseDTO>> updateRecurringPlan(
            @Parameter(description = "Plan ID", required = true) Long id,
            @RequestBody RecurringPlanUpdateDTO recurringPlanUpdateDTO
    );

    @Operation(summary = "Activate plan")
    @OkResponseCode
    @NotFoundResponseCode
    ResponseEntity<ContentWrapperResponseDTO<RecurringPlanResponseDTO>> activatePlan(
            @Parameter(description = "Plan ID", required = true) Long id
    );

    @Operation(summary = "Deactivate plan")
    @OkResponseCode
    @NotFoundResponseCode
    ResponseEntity<ContentWrapperResponseDTO<RecurringPlanResponseDTO>> deactivatePlan(
            @Parameter(description = "Plan ID", required = true) Long id
    );

    @Operation(summary = "Delete plan")
    @NoContentResponseCode
    @NotFoundResponseCode
    @ConflictResponseCode
    @InternalServerErrorResponseCode
    ResponseEntity<Void> deletePlan(@Parameter(description = "Plan ID", required = true) Long id);
}
