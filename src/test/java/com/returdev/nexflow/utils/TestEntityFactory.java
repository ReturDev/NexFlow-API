package com.returdev.nexflow.utils;

import com.returdev.nexflow.model.entities.*;
import com.returdev.nexflow.model.enums.*;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;

public class TestEntityFactory {

    public static CategoryEntity createValidCategory() {
        LocalDateTime date = LocalDateTime.now();
        return CategoryEntity.builder()
                .name("Test")
                .iconResource("icon_resource")
                .createdAt(date)
                .updatedAt(date)
                .build();
    }

    public static UserEntity createValidUser() {
        LocalDateTime date = LocalDateTime.now();
        return UserEntity.builder()
                .name("User")
                .surnames("Test")
                .email("test@email.com")
                .password("12345678")
                .role(Role.USER)
                .createdAt(date)
                .updatedAt(date)
                .build();
    }

    public static WalletEntity createValidWallet(UserEntity owner) {
        LocalDateTime date = LocalDateTime.now();
        return WalletEntity.builder()
                .name("Test Wallet")
                .balanceInCents(20000L)
                .currencyCode("EUR")
                .overdraftLimit(20000L)
                .user(owner)
                .createdAt(date)
                .updatedAt(date)
                .build();
    }

    public static TransactionEntity createValidTransaction(
            CategoryEntity category,
            WalletEntity wallet,
            RecurringPlanEntity plan
    ) {
        LocalDateTime date = LocalDateTime.now();

        return TransactionEntity.builder()
                .title("Transaction")
                .description("description")
                .balanceInCents(200L)
                .type(TransactionType.EXPENSE)
                .date(date)
                .status(TransactionStatus.COMPLETED)
                .category(category)
                .wallet(wallet)
                .plan(plan)
                .createdAt(date)
                .updatedAt(date)
                .build();

    }

    public static RecurringPlanEntity createValidRecurringPlan(
            CategoryEntity category,
            WalletEntity wallet
    ) {

        LocalDateTime date = LocalDateTime.now();

        return RecurringPlanEntity.builder()
                .title("Plan")
                .description("description")
                .balanceInCents(2000L)
                .type(TransactionType.EXPENSE)
                .startDate(date)
                .frequency(Frequency.DAILY)
                .interval(1)
                .nextExecutionDate(date.plusDays(1))
                .status(PlanStatus.ACTIVE)
                .endDate(date.plusDays(2L))
                .category(category)
                .wallet(wallet)
                .createdAt(date)
                .updatedAt(date)
                .build();

    }


}
