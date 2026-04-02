package com.returdev.nexflow.model.exceptions;

import lombok.Getter;

/**
 * Base exception class for all domain-specific business logic errors.
 */
@Getter
public class BusinessException extends RuntimeException {

    private final String code;
    private final Object[] args;

    /**
     * Constructs a new BusinessException with a specific error code and
     * dynamic arguments.
     *
     * @param code the message key used for translation.
     * @param args optional arguments for message formatting.
     */
    public BusinessException(String code, Object... args) {
        super(code);
        this.code = code;
        this.args = args;
    }
}

