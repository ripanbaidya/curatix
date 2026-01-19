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
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Handles authorization failures (HTTP 403 Forbidden).
 * <p>Invoked when an authenticated user with a valid JWT attempts to
 * access a secured endpoint without sufficient roles or authorities.
 * <p>Typically triggered by method or endpoint security such as
 * {@link PreAuthorize}, {@link PostAuthorize}, or access rules.
 * <p>Ensures a standardized JSON error response instead of the default HTML error page.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void handle(
            final @NonNull HttpServletRequest request,
            final @NonNull HttpServletResponse response,
            final @NonNull AccessDeniedException accessDeniedException) throws IOException, ServletException {

        log.warn("Access denied to {} for user from {} - Reason: {}", request.getRequestURI(),
                request.getRemoteAddr(), accessDeniedException.getMessage())
        ;

        // Build error response
        ErrorDetail errorDetail = ErrorDetail.builder()
                .type(ErrorCode.ACCESS_DENIED.getErrorType())
                .code(ErrorCode.ACCESS_DENIED.getErrorCode())
                .detail(ErrorCode.ACCESS_DENIED.getDefaultMessage())
                .build();
        ErrorResponse errorResponse = ErrorResponse.of(errorDetail);

        // Set Response properties
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        // Write the json response
        objectMapper.writeValue(response.getOutputStream(), errorResponse);
    }
}
