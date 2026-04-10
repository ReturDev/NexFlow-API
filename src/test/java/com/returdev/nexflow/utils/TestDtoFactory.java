package com.returdev.nexflow.utils;

import com.returdev.nexflow.dto.request.*;
import com.returdev.nexflow.dto.request.update.*;
import com.returdev.nexflow.dto.response.*;
import com.returdev.nexflow.model.enums.*;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.UUID;

public class TestDtoFactory {

    public static CategoryRequestDTO createValidCategoryRequestDTO() {
        return new CategoryRequestDTO("Category", "icon_resource");
    }

    public static CategoryUpdateDTO createValidCategoryUpdateDTO() {
        return new CategoryUpdateDTO("New Category", "new_icon_resource");
    }

    public static CategoryResponseDTO createValidCategoryResponseDTO() {
        LocalDateTime date = LocalDateTime.now();
        return new CategoryResponseDTO(
                1L,
                "Category",
                "icon_resource",
                date,
                date
        );
    }

    public static UserRequestDTO createValidUserRequestDTO() {
        return new UserRequestDTO(
                "User",
                "Test",
                "email@email.com",
                "password"
        );
    }

    public static UserResponseDTO createValidUserResponseDTO() {
        LocalDateTime date = LocalDateTime.now();
        return new UserResponseDTO(
                UUID.randomUUID(),
                "User",
                "test",
                Role.USER,
                "email@email.com",
                date,
                date
        );
    }

    public static UserUpdateDTO createValidUserUpdateDTO() {
        return new UserUpdateDTO(
                "New name",
                "New surname"
        );
    }

    public static WalletRequestDTO createValidWalletRequestDTO(UUID userId) {
        return new WalletRequestDTO(
                "Wallet",
                "EUR",
                100L,
                userId
        );
    }

    public static WalletUpdateDTO createValidWalletUpdateDTO() {
        return new WalletUpdateDTO(
                "New name",
                "ARG",
                100L
        );
    }

    public static WalletResponseDTO createValidWalletResponseDTO() {
        LocalDateTime date = LocalDateTime.now();
        return new WalletResponseDTO(
                1L,
                "Wallet",
                0L,
                "EUR",
                0L,
                date,
                date
        );
    }

    public static TransactionRequestDTO createValidTransactionRequestDTO(Long categoryId, Long walletId) {
        return new TransactionRequestDTO(
                "Title",
                "description",
                100L,
                TransactionType.INCOME,
                OffsetDateTime.now(),
                categoryId,
                walletId
        );
    }

    public static TransactionUpdateDTO createValidTransactionUpdateDTO(Long categoryId) {
        return new TransactionUpdateDTO(
                "New title",
                "New description",
                100L,
                TransactionType.EXPENSE,
                OffsetDateTime.now().plusDays(1),
                categoryId
        );
    }

    public static TransactionResponseDTO createValidTransactionResponseDTO(
            CategoryResponseDTO categoryResponse,
            Long walletId,
            Long planId
    ) {
        LocalDateTime date = LocalDateTime.now();
        return new TransactionResponseDTO(
                1L,
                "Transaction",
                "description",
                0L,
                TransactionType.EXPENSE,
                date,
                TransactionStatus.COMPLETED,
                categoryResponse,
                walletId,
                planId,
                date,
                date
        );
    }

    public static RecurringPlanRequestDTO createValidPlanRequestDTO(Long categoryId, Long walletId) {
        OffsetDateTime startDate = OffsetDateTime.now();
        return new RecurringPlanRequestDTO(
                "Title",
                "description",
                100L,
                TransactionType.EXPENSE,
                startDate,
                Frequency.DAILY,
                1,
                startDate.plusDays(1),
                categoryId,
                walletId
        );
    }

    public static RecurringPlanUpdateDTO createValidPlanUpdateDTO(Long categoryId) {
        OffsetDateTime startDate = OffsetDateTime.now();
        return new RecurringPlanUpdateDTO(
                "Title",
                "description",
                100L,
                TransactionType.EXPENSE,
                startDate,
                Frequency.DAILY,
                1,
                startDate.plusDays(1),
                categoryId
        );

    }

    public static RecurringPlanResponseDTO createValidPlanResponseDTO() {
        LocalDateTime date = LocalDateTime.now();
        return new RecurringPlanResponseDTO(
                1L,
                "Title",
                "description",
                100L,
                TransactionType.EXPENSE,
                date,
                Frequency.MONTHLY,
                1,
                date,
                PlanStatus.ACTIVE,
                date,
                null,
                null,
                date,
                date
        );

    }

    public static PasswordUpdateDTO createValidPasswordUpdate() {
        return new PasswordUpdateDTO("old_password", "new_password");
    }


}
