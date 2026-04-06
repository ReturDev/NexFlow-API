package com.returdev.nexflow.advice.manager;

/**
 * Service interface for managing localized application messages.
 */
public interface MessageManager {

    /**
     * Retrieves a localized message for the given resource key.
     *
     * @param resourceKey the unique identifier in the message bundle
     *                    (e.g., "exception.user.not_found").
     * @return the translated string based on the current request locale.
     */
    String getMessage(String resourceKey);

    /**
     * Retrieves a localized message and interpolates it with the provided arguments.
     *
     * @param resourceKey the message bundle key (e.g., "wallet.limit.exceeded").
     * @param params      an array of objects to be inserted into the message
     *                    placeholders (e.g., {0}, {1}).
     * @return the formatted and translated string.
     */
    String getMessageWithParams(String resourceKey, Object[] params);

}
