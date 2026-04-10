package com.returdev.nexflow.advice;

import com.returdev.nexflow.advice.manager.MessageManager;
import com.returdev.nexflow.model.exceptions.InvalidPasswordException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Global API exception handler dedicated to security and authentication failures.
 */
@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SecurityExceptionHandler {

    private final MessageManager messageManager;

    /**
     * Handles authentication failures caused by incorrect or invalid passwords.
     *
     * @param ex the caught {@link InvalidPasswordException}.
     * @return a {@link ProblemDetail} containing the localized security error message.
     */
    @ExceptionHandler(InvalidPasswordException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ProblemDetail handleInvalidPasswordException(InvalidPasswordException ex) {
        return ProblemDetail.forStatusAndDetail(
                HttpStatus.UNAUTHORIZED,
                messageManager.getMessage(ex.getCode())
        );
    }

}
