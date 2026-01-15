package com.curatix.security.authentication;

import com.curatix.security.exception.JwtAuthenticationException;
import com.curatix.security.jwt.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final HandlerExceptionResolver handlerExceptionResolver;

    @Override
    protected void doFilterInternal(
            @NonNull final HttpServletRequest request,
            @NonNull final HttpServletResponse response,
            @NonNull final FilterChain filterChain) throws ServletException, IOException {

        try {
            // Extract Jwt token from Authorization header
            String jwt = extractJwtFromRequest(request);
            if (jwt == null) {
                filterChain.doFilter(request, response);
                return;
            }

            // Already authenticated - skip (defensive check for stateless setup)
            if (SecurityContextHolder.getContext().getAuthentication() != null) {
                filterChain.doFilter(request, response);
                return;
            }

            // Validate token and authenticate
            authenticateRequest(jwt, request);

            // Continue with authenticated context
            filterChain.doFilter(request, response);

        } catch (Exception ex) {
            log.error("Authentication error for {} - {}: {}", request.getRequestURI(),
                    ex.getClass().getSimpleName(), ex.getMessage()
            );
            handlerExceptionResolver.resolveException(request, response, null, ex);
        }
    }

    /**
     * Path to exclude
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // Remove context path prefix: /api/v1
        String path = request.getRequestURI().replaceFirst("^/api/v1", "");

        return path.startsWith("/auth") || path.startsWith("/error") ||
                path.startsWith("/test") || path.startsWith("/actuator/health") ||
                path.startsWith("/swagger-ui") || path.startsWith("/v3/api-docs");
    }
    


    /* Helper Methods */

    /**
     * Extract Jwt access token from the request
     */
    private String extractJwtFromRequest(HttpServletRequest request) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(header) && header.startsWith(BEARER_PREFIX)) {
            return header.substring(BEARER_PREFIX.length()).trim();
        }
        return null;
    }

    /**
     * Authenticates request using JWT token.
     *
     * @throws JwtAuthenticationException if token is invalid/expired (from JwtService)
     * @throws UsernameNotFoundException  if user doesn't exist
     */
    private void authenticateRequest(String jwt, HttpServletRequest request) {
        if (!jwtService.isTokenValid(jwt)) {
            log.debug("Token validation returned false for request to {}", request.getRequestURI());
        }

        String email = jwtService.extractEmail(jwt);
        if (!StringUtils.hasText(email)) {
            log.warn("JWT token missing subject for request from {}", request.getRemoteAddr());
            // Skip authentication, continue as anonymous
            return;
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );

        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        log.debug("Successfully authenticated user: {} for {}", email, request.getRequestURI());
    }
}