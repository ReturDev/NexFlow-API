package com.returdev.nexflow.advice.manager;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

/**
 * Implementation of {@link MessageManager} using Spring's {@link MessageSource}.
 */
@Component
@RequiredArgsConstructor
public class MessageManagerImpl implements MessageManager {

    private final MessageSource messageSource;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMessage(String resourceKey) {
        return getMessageWithParams(resourceKey, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMessageWithParams(String resourceKey, Object[] params) {
        return messageSource.getMessage(resourceKey, params, LocaleContextHolder.getLocale());
    }
}
