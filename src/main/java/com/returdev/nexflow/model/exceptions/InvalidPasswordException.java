package com.returdev.nexflow.model.exceptions;

/**
 * Thrown when a password-related operation fails due to validation or verification errors.
 */
public class InvalidPasswordException extends BusinessException {

    /**
     * Constructs a new InvalidPasswordException with a specific error code for translation.
     *
     * @param code the message bundle key used to look up the localized error description.
     */
    public InvalidPasswordException(String code) {
        super(code);
    }
}
