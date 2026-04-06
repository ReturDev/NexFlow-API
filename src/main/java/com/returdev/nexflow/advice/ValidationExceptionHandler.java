package com.returdev.nexflow.advice;

import com.returdev.nexflow.advice.manager.MessageManager;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MarkerFactory;
import org.springframework.context.MessageSourceAware;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import tools.jackson.databind.exc.InvalidFormatException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Global API error handler for validation and data integrity exceptions.
 */
@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice
public class ValidationExceptionHandler {

    private final MessageManager messageManager;
    private final MessageSourceAware messageSourceAware;


    /**
     * Handles {@link ConstraintViolationException} typically thrown by
     * {@code @Validated} on service layer methods or path variables.
     *
     * @param ex the caught constraint violation exception.
     * @return a {@link ProblemDetail} with a map of field names to error messages.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail handleConstraintViolationException(ConstraintViolationException ex) {
        Map<String, String> errorMessages = new HashMap<>();

        ex.getConstraintViolations().forEach(violation -> {

                    String fieldPath = violation.getPropertyPath().toString();

                    errorMessages.put(
                            fieldPath.substring(fieldPath.lastIndexOf(".") + 1),
                            violation.getMessage()
                    );

                }

        );

        showConsoleValidationInfo(errorMessages.toString());

        return getProblemDetailWithErrors(errorMessages);

    }

    /**
     * Handles {@link MethodArgumentNotValidException} thrown when {@code @Valid}
     * fails on a {@code @RequestBody} controller parameter.
     *
     * @param ex the binding result containing field errors.
     * @return a {@link ProblemDetail} containing a structured map of all field-level errors.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {

        Map<String, String> errorMessages = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error ->
                errorMessages.put(
                        error.getField(),
                        error.getDefaultMessage()
                )
        );

        showConsoleValidationInfo(errorMessages.toString());

        return getProblemDetailWithErrors(errorMessages);

    }

    /**
     * Handles malformed JSON requests, with specialized logic for invalid Enum values.
     *
     * @param ex the JSON parsing exception.
     * @return a {@link ProblemDetail} explaining the syntax error or invalid enum constant.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {

        if (ex.getCause() instanceof InvalidFormatException ife && ife.getTargetType().isEnum()) {
            return buildEnumErrorProblemDetail(ife);
        }

        showConsoleValidationInfo("JSON malformed");

        return ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                messageManager.getMessage("exception.json.request_malformed")
        );

    }

    /**
     * Handles type mismatch errors when a request parameter cannot be converted to the expected Java type.
     *
     * @param ex the caught {@link MethodArgumentTypeMismatchException}.
     * @return a {@link ProblemDetail} with a 400 Bad Request status and localized type information.
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        Class<?> requiredType = ex.getRequiredType();
        String requiredTypeName = requiredType != null ? requiredType.getSimpleName() : "Unspecified";
        String message = messageManager.getMessageWithParams(
                "exception.generic.type_mismatch",
                new String[]{requiredTypeName}
        );

        showConsoleValidationInfo(message);

        return ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                message
        );
    }

    /**
     * Catches general illegal argument exceptions and returns them as bad requests.
     *
     * @param ex the {@link IllegalArgumentException} thrown by the business or service layer.
     * @return a {@link ProblemDetail} containing the exception's raw message.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail handleIllegalArgumentException(IllegalArgumentException ex) {

        showConsoleValidationInfo(Arrays.toString(ex.getStackTrace()));

        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    /**
     * Constructs a specialized error response when a JSON value fails to map to a Java Enum.
     *
     * @param ex the {@link InvalidFormatException} usually triggered during Jackson deserialization.
     * @return a {@link ProblemDetail} explaining exactly which value was wrong and what the valid options are.
     */
    private ProblemDetail buildEnumErrorProblemDetail(InvalidFormatException ex) {

        String invalidValue = ex.getValue().toString();

        Object[] enumConstants = ex.getTargetType().getEnumConstants();

        List<String> validValues = Arrays.stream(enumConstants).map(Object::toString).toList();

        String acceptedValues = String.join(", ", validValues.subList(0, validValues.size() - 1))
                + " or " + validValues.getLast();

        showConsoleValidationInfo("Invalid Enum Value");

        return ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                messageManager.getMessageWithParams(
                        "exception.invalid_enum_value.message",
                        new String[]{invalidValue, acceptedValues}
                )
        );


    }

    /**
     * Logs validation failure information to the console/log file using a specific SLF4J Marker.
     *
     * @param info the validation error details to be logged.
     */
    private void showConsoleValidationInfo(String info) {

        log.info(MarkerFactory.getMarker("Validation Errors"), info);

    }

    /**
     * Creates a standardized validation error response containing a map of field-specific errors.
     *
     * @param errors a map where the key is the field name and the value is the validation message.
     * @return a {@link ProblemDetail} with a 400 Bad Request and the detailed error map.
     */
    private ProblemDetail getProblemDetailWithErrors(Map<String, String> errors) {

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                messageManager.getMessage("exception.validation.failed.message")
        );
        problemDetail.setProperty("errors", errors);

        return problemDetail;

    }

}
