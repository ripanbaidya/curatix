package com.curatix.security.handler;

import com.curatix.common.constant.ErrorCode;
import com.curatix.common.response.ErrorDetail;
import com.curatix.common.response.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Handles unauthenticated access attempts (HTTP 401 Unauthorized).
 * <p>This handler is invoked when authentication fails, such as when no
 * JWT is provided, the token is expired, malformed, or invalid, or when
 * JWT signature verification fails.</p>
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(
            @NonNull final HttpServletRequest request,
            @NonNull final HttpServletResponse response,
            @NonNull final AuthenticationException authException) throws IOException, ServletException {

        log.warn("Unauthorized access attempt to {} from {} - Reason: {}", request.getRequestURI(),
                request.getRemoteAddr(), authException.getMessage()
        );
        // Determine errorcode based on exception message
        ErrorCode errorCode = determineErrorCode(authException);
        // Build the error details
        ErrorDetail detail = ErrorDetail.builder()
                .type(errorCode.getErrorType())
                .code(errorCode.getErrorCode())
                .detail(errorCode.getDefaultMessage())
                .path(request.getRequestURI())
                .build();
        ErrorResponse errorResponse = ErrorResponse.of(detail);

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        // Write JSON Response
        objectMapper.writeValue(response.getOutputStream(), errorResponse);
    }

    /**
     * Determines specific error code based on exception type/message.
     *
     * @param authException The authentication exception
     * @return Appropriate ErrorCode
     */
    private ErrorCode determineErrorCode(AuthenticationException authException) {
        String message = authException.getMessage().toLowerCase();
        if (message.contains("expired")) {
            return ErrorCode.TOKEN_EXPIRED;
        } else if (message.contains("invalid") || message.contains("malformed")) {
            return ErrorCode.TOKEN_INVALID;
        } else if (message.contains("missing") || message.contains("required")) {
            return ErrorCode.TOKEN_MISSING;
        }
        // Default to invalid credentials
        return ErrorCode.INVALID_CREDENTIALS;
    }
}
