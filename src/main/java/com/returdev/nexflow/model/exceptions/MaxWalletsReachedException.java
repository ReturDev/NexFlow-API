package com.returdev.nexflow.model.exceptions;

/**
 * Thrown when a user attempts to create a new wallet but has already reached
 * the system-defined maximum capacity.
 */
public class MaxWalletsReachedException extends BusinessException {

    /**
     * Constructs a new MaxWalletsReachedException with the specific system limit.
     *
     * @param limit the maximum number of wallets allowed per user.
     */
    public MaxWalletsReachedException(int limit) {
        super("exception.wallet.max_limit_reached", limit);
    }

}