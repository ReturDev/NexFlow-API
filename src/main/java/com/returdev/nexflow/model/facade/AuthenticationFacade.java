package com.returdev.nexflow.model.facade;

import com.returdev.nexflow.model.entities.UserEntity;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Facade for accessing the current security context and authenticated user information.
 */
@Component
public class AuthenticationFacade {

    /**
     * Retrieves the authenticated user from the current Security Context.
     *
     * @return the {@link UserEntity} associated with the current request.
     * @throws AuthenticationServiceException if the security context is empty or
     * if the principal is not a valid {@link UserEntity} instance.
     */
    public UserEntity getAuthenticateUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.getPrincipal() instanceof UserEntity user) {
            return user;
        }

        throw new AuthenticationServiceException("The user was not found in the context");
    }

}