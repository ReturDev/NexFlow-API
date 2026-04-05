package com.returdev.nexflow.model.exceptions;

public class ResourceNotFoundException extends BusinessException {
    /**
     * Constructs a new BusinessException with a specific error code and
     * dynamic arguments.
     *
     * @param code the message key used for translation.
     * @param args optional arguments for message formatting.
     */
    public ResourceNotFoundException(String code, Object... args) {
        super(code, args);
    }
}
