package com.returdev.nexflow.model.enums

/**
 * Categorizes a transaction based on the direction of cash flow.
 * This enum differentiates between funds entering the account
 * and funds leaving the account for accounting and reporting purposes.
 */
enum class TransactionType {
    INCOME,
    EXPENSE;
}