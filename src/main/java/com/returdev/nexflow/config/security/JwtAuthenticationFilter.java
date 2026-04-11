package com.returdev.nexflow.config.security;

import com.returdev.nexflow.services.jwt.JwtService;
import com.returdev.nexflow.services.user.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * HTTP Filter that intercepts every request to validate JWT security tokens.
 */
@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserService userService;


    /**
     * Performs the core filtering logic to authenticate requests via JWT.
     * <p>
     * <b>Workflow:</b>
     * <ol>
     * <li>Extracts the token from the {@code Authorization} header.</li>
     * <li>If no token is present, continues the filter chain (anonymous access).</li>
     * <li>Extracts the username (email) from the token.</li>
     * <li>If the user is not yet authenticated in the current context, loads user details.</li>
     * <li>Validates the token integrity and expiration.</li>
     * <li>If valid, creates a {@link UsernamePasswordAuthenticationToken} and stores it
     * in the {@link SecurityContextHolder}.</li>
     * </ol>
     * </p>
     *
     * @param request     the incoming {@link HttpServletRequest}.
     * @param response    the outgoing {@link HttpServletResponse}.
     * @param filterChain the chain of remaining filters to execute.
     * @throws ServletException if a servlet-specific error occurs.
     * @throws IOException      if an I/O error occurs during request processing.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        final String jwt = getToken(request.getHeader(HttpHeaders.AUTHORIZATION));

        if (jwt == null) {
            filterChain.doFilter(request, response);
            return;
        }

        String userEmail = jwtService.extractEmail(jwt);

        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            UserDetails userDetails = userService.loadUserByUsername(userEmail);

            if (jwtService.isTokenValid(jwt, userDetails)) {

                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );

                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            }

        }

        filterChain.doFilter(request, response);

    }

    /**
     * Helper method to parse the "Bearer " prefix from the Authorization header.
     *
     * @param authHeader the raw value of the {@code Authorization} header.
     * @return the JWT string if valid, or {@code null} if the header is missing or malformed.
     */
    private String getToken(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

}
