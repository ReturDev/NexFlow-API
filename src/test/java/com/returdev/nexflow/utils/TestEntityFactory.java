package com.returdev.nexflow.utils;

import com.returdev.nexflow.model.entities.*;
import com.returdev.nexflow.model.enums.*;

import java.time.LocalDateTime;

public class TestEntityFactory {

    public static CategoryEntity createValidCategory() {
        return CategoryEntity.builder()
                .name("Test")
                .build();
    }

    public static UserEntity createValidUser() {
        return UserEntity.builder()
                .name("User")
                .surnames("Test")
                .email("test@email.com")
                .password("12345678")
                .role(Role.USER)
                .build();
    }

    public static WalletEntity createValidWallet(UserEntity owner) {
        return WalletEntity.builder()
                .name("Test Wallet")
                .balanceInCents(20000L)
                .currencyCode("EUR")
                .overdraftLimit(20000L)
                .user(owner)
                .build();
    }

    public static TransactionEntity createValidTransaction(
            CategoryEntity category,
            WalletEntity wallet,
            RecurringPlanEntity plan
    ) {
        return TransactionEntity.builder()
                .title("Transaction")
                .description("")
                .balanceInCents(200L)
                .type(TransactionType.EXPENSE)
                .date(LocalDateTime.now())
                .status(TransactionStatus.COMPLETED)
                .category(category)
                .wallet(wallet)
                .plan(plan)
                .build();

    }

    public static RecurringPlanEntity createValidRecurringPlan(
            CategoryEntity category,
            WalletEntity wallet
    ) {

        return RecurringPlanEntity.builder()
                .title("Plan")
                .description("")
                .balanceInCents(2000L)
                .type(TransactionType.EXPENSE)
                .startDate(LocalDateTime.now())
                .frequency(Frequency.DAILY)
                .interval(1)
                .nextExecutionDate(LocalDateTime.now())
                .status(PlanStatus.ACTIVE)
                .category(category)
                .wallet(wallet)
                .build();

    }


}
