package com.returdev.nexflow.model.exceptions;

/**
 * Thrown when a withdrawal or expense transaction would result in a balance
 * that exceeds the wallet's configured overdraft protection.
 */
public class OverdraftLimitException extends BusinessException {

    /**
     * Constructs a new OverdraftLimitException with detailed balance context.
     *
     * @param resultingBalance the calculated balance if the transaction were to proceed (in cents).
     * @param overdraftLimit   the maximum allowed negative balance for this wallet (in cents).
     */
    public OverdraftLimitException(Long resultingBalance, Long overdraftLimit) {
        super("exception.wallet.overdraft_limit_exceeded", resultingBalance, overdraftLimit);
    }

}
