package com.returdev.nexflow.model.enums;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Defines the possible states of a financial or system transaction.
 * This enum is used to track the lifecycle and current progress of
 * individual transaction records.
 */
@Schema(enumAsRef = true)
public enum TransactionStatus {

    COMPLETED,
    PENDING;

}
