package com.curatix.common.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Detailed error information in error responses.
 * <p>Follows {@code RFC 7807} Problem Details for HTTP APIs (simplified).
 *
 * @param type           Error category/type (e.g., VALIDATION_ERROR)
 * @param code           Specific error code(e.g., USER.NOT_FOUND, AUTH.INVALID_TOKEN)
 * @param title          Human-readable error title
 * @param status         HTTP status code
 * @param detail         Specific error message with context
 * @param timestamp      When the error occurred (UTC)
 * @param path           API endpoint where error occurred
 * @param errors         List of field-level errors (for validation)
 * @param traceId        Correlation ID for debugging (500 errors only)
 * @param retryAfter     Seconds to wait before retry (rate limits)
 * @param allowedMethods Allowed HTTP methods (405 errors)
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorDetail(
        String type,
        String code,
        String title,
        int status,
        String detail,
        @JsonFormat(shape = JsonFormat.Shape.STRING)
        Instant timestamp,
        String path,
        List<FieldError> errors,
        String traceId,
        Integer retryAfter,
        List<String> allowedMethods
) {
    /**
     * Builder for ErrorDetail to simplify construction
     */
    public static class Builder {
        private ErrorType errorType;
        private String code;
        private String detail;
        private String path;
        private List<FieldError> errors;
        private String traceId;
        private Integer retryAfter;
        private List<String> allowedMethods;

        public Builder type(ErrorType errorType) {
            this.errorType = errorType;
            return this;
        }

        public Builder code(String code) {
            this.code = code;
            return this;
        }

        public Builder detail(String detail) {
            this.detail = detail;
            return this;
        }

        public Builder path(String path) {
            this.path = path;
            return this;
        }

        public Builder errors(List<FieldError> errors) {
            this.errors = errors;
            return this;
        }

        public Builder traceId(String traceId) {
            this.traceId = traceId;
            return this;
        }

        public Builder retryAfter(Integer retryAfter) {
            this.retryAfter = retryAfter;
            return this;
        }

        public Builder allowedMethods(List<String> allowedMethods) {
            this.allowedMethods = allowedMethods;
            return this;
        }

        public ErrorDetail build() {
            return new ErrorDetail(
                    errorType.toString(),
                    code,
                    errorType.getTitle(),
                    errorType.getStatusCode(),
                    detail,
                    Instant.now(),
                    path,
                    errors,
                    traceId,
                    retryAfter,
                    allowedMethods
            );
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}