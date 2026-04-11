package com.returdev.nexflow.services.jwt;

import io.jsonwebtoken.Claims;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Map;
import java.util.function.Function;

/**
 * Service interface for handling JSON Web Token (JWT) operations.
 */
public interface JwtService {

    /**
     * Generates a standard Access Token for the specified user.
     *
     * @param userDetails the authenticated user's details.
     * @return a signed JWT string.
     */
    String generateToken(UserDetails userDetails);

    /**
     * Generates an Access Token with custom claims (e.g., roles, permissions, or
     * custom IDs) included in the payload.
     *
     * @param extraClaims a map of additional key-value pairs to include in the JWT.
     * @param userDetails the authenticated user's details.
     * @return a signed JWT string containing the extra claims.
     */
    String generateToken(Map<String, Object> extraClaims, UserDetails userDetails);

    /**
     * Generates a long-lived Refresh Token for the specified user.
     *
     * @param userDetails the authenticated user's details.
     * @return a signed JWT refresh token string.
     */
    String generateRefreshToken(UserDetails userDetails);

    /**
     * Extracts the user's email (subject) from the provided token.
     *
     * @param token the JWT string.
     * @return the email address encoded in the token's subject field.
     */
    String extractEmail(String token);

    /**
     * Extracts a specific claim from the token's payload using a functional resolver.
     *
     * @param <T>            the expected return type of the claim.
     * @param token          the JWT string.
     * @param claimsResolver a function that defines how to retrieve the claim
     *                       from the {@link Claims} object.
     * @return the value of the extracted claim.
     */
    <T> T extractClaim(String token, Function<Claims, T> claimsResolver);

    /**
     * Validates that the provided token belongs to the given user and has not expired.
     *
     * @param token       the JWT string to check.
     * @param userDetails the user details to compare against the token's subject.
     * @return {@code true} if the token is valid and unexpired; {@code false} otherwise.
     */
    boolean isTokenValid(String token, UserDetails userDetails);

}
