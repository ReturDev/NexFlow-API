package com.returdev.nexflow.advice;

import com.returdev.nexflow.advice.manager.MessageManager;
import com.returdev.nexflow.model.exceptions.*;
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
 * Global API exception handler for domain-specific business exceptions.
 */
@RequiredArgsConstructor
@Slf4j
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class BusinessExceptionHandler {

    private final MessageManager messageManager;

    /**
     * Handles cases where a requested resource (User, Wallet, Transaction) does not exist.
     *
     * @param ex the caught {@link ResourceNotFoundException}.
     * @return a {@link ProblemDetail} with {@code 404 Not Found} and a localized message.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ProblemDetail handleResourceNotFoundException(ResourceNotFoundException ex) {
        return ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                messageManager.getMessageWithParams(ex.getCode(), ex.getArgs())
        );
    }

    /**
     * Handles chronological inconsistencies, such as start dates appearing after end dates.
     *
     * @param ex the caught {@link DateConflictException}.
     * @return a {@link ProblemDetail} with {@code 409 Conflict} via {@link #getConflictProblemDetail}.
     */
    @ExceptionHandler(DateConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ProblemDetail handleDateConflictException(DateConflictException ex) {
        return getConflictProblemDetail(ex);
    }

    /**
     * Handles unique constraint violations, such as attempting to register with an existing email.
     *
     * @param ex the caught {@link FieldAlreadyExistException}.
     * @return a {@link ProblemDetail} with {@code 409 Conflict} via {@link #getConflictProblemDetail}.
     */
    @ExceptionHandler(FieldAlreadyExistException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ProblemDetail handleFieldAlreadyExistException(FieldAlreadyExistException ex) {
        return getConflictProblemDetail(ex);
    }

    /**
     * Handles business limit violations when a user reaches the maximum allowed wallet count.
     *
     * @param ex the caught {@link MaxWalletsReachedException}.
     * @return a {@link ProblemDetail} with {@code 409 Conflict} via {@link #getConflictProblemDetail}.
     */
    @ExceptionHandler(MaxWalletsReachedException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ProblemDetail handleMaxWalletsReachedException(MaxWalletsReachedException ex) {
        return getConflictProblemDetail(ex);
    }

    /**
     * Handles financial safety violations when a transaction would exceed a wallet's overdraft limit.
     *
     * @param ex the caught {@link OverdraftLimitException}.
     * @return a {@link ProblemDetail} with {@code 409 Conflict} via {@link #getConflictProblemDetail}.
     */
    @ExceptionHandler(OverdraftLimitException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ProblemDetail handleOverdraftLimitException(OverdraftLimitException ex) {
        return getConflictProblemDetail(ex);
    }

    /**
     * Centralized utility to build a {@code 409 Conflict} response for {@link BusinessException} types.
     *
     * @param ex the business exception containing error codes and metadata.
     * @return a standardized {@link ProblemDetail} for business logic conflicts.
     */
    private ProblemDetail getConflictProblemDetail(BusinessException ex) {
        return ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT,
                messageManager.getMessageWithParams(ex.getCode(), ex.getArgs())
        );
    }

}
