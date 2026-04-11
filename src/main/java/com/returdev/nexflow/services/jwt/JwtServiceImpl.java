package com.returdev.nexflow.services.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;


/**
 * Implementation of {@link JwtService} for generating and managing JSON Web Tokens.
 */
@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {

    private static final long ACCESS_TOKEN_EXPIRATION = Duration.ofMinutes(15).toMillis();
    private static final long REFRESH_TOKEN_EXPIRATION = Duration.ofDays(5).toMillis();


    @Value("${spring.jwt.private-key}")
    private String privateKey;

    @Value("{spring.jwt.issuer}")
    private String issuer;

    /** {@inheritDoc} */
    @Override
    public String generateToken(UserDetails userDetails) {
        return generateToken(null, userDetails);
    }

    /** {@inheritDoc} */
    @Override
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return buildToken(extraClaims, userDetails, ACCESS_TOKEN_EXPIRATION);
    }

    /** {@inheritDoc} */
    @Override
    public String generateRefreshToken(UserDetails userDetails) {
        return buildToken(new HashMap<>(), userDetails, REFRESH_TOKEN_EXPIRATION);
    }

    /**
     * Core logic to construct a JWT with specified claims and expiration.
     *
     * @param claims     the key-value pairs to include in the payload.
     * @param userDetails the user for whom the token is issued.
     * @param expiration duration in milliseconds until the token becomes invalid.
     * @return a compact, URL-safe JWT string.
     */
    private String buildToken(Map<String, Object> claims, UserDetails userDetails, long expiration) {
        return Jwts.builder()
                .claims(claims)
                .subject(userDetails.getUsername())
                .issuer(issuer)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey()) // JJWT detecta el algoritmo (HS256) por el tamaño de la llave
                .compact();
    }

    /** {@inheritDoc} */
    @Override
    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /** {@inheritDoc} */
    @Override
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractClaims(token);
        return claimsResolver.apply(claims);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String email = extractEmail(token);
        return (email.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    /**
     * Decodes the Base64-encoded private key and prepares it for HMAC signing.
     *
     * @return a {@link SecretKey} derived from the configured private key.
     */
    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(privateKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Parses the JWT string and verifies its signature to retrieve the payload.
     *
     * @param token the JWT string to parse.
     * @return the {@link Claims} object representing the token's payload.
     * @throws io.jsonwebtoken.JwtException if the token is tampered with or malformed.
     */
    private Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Helper to check if the current system time has passed the token's expiration date.
     *
     * @param token the JWT string.
     * @return {@code true} if the token has expired.
     */
    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

}
