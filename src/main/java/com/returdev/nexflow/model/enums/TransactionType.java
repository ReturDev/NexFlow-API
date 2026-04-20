package com.returdev.nexflow.model.enums;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Categorizes a transaction based on the direction of cash flow.
 * This enum differentiates between funds entering the account
 * and funds leaving the account for accounting and reporting purposes.
 */
@Schema(enumAsRef = true)
public enum TransactionType {
    INCOME,
    EXPENSE;
}