package com.returdev.nexflow.model.exceptions;

/**
 * Exception thrown when a security token (JWT or Refresh Token) is structurally invalid,
 * expired, or blacklisted.
 */
public class InvalidTokenException extends BusinessException {

    /**
     * Constructs a new InvalidTokenException with a specific localization code.
     * @param code the resource bundle key used to describe the token error.
     */
    public InvalidTokenException(String code) {
        super(code);
    }

}
