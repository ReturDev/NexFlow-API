package com.returdev.nexflow.utils;

import com.returdev.nexflow.dto.request.*;
import com.returdev.nexflow.dto.request.update.*;
import com.returdev.nexflow.model.enums.Frequency;
import com.returdev.nexflow.model.enums.TransactionType;
import org.springframework.cglib.core.Local;

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

    public static UserRequestDTO createValidUserRequestDTO() {
        return new UserRequestDTO(
                "User",
                "Test",
                "email@email.com",
                "password"
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
                0L,
                TransactionType.EXPENSE,
                OffsetDateTime.now().plusDays(1),
                categoryId
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

}
