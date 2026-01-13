package com.curatix.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Representation of individual field-level validation errors.
 * <p>Used in validation error responses to provide detailed feedback.
 *
 * @param field         Field name that failed validation
 * @param message       Human-readable error message
 * @param rejectedValue The invalid value submitted (nullable)
 * @param code          Optional machine-readable validation code
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder(value = {
        "field",
        "message",
        "rejectedValue",
        "code"
})
public record FieldError(
        String field,
        String message,
        Object rejectedValue,
        String code
) {
    /**
     * Creates a FieldError without rejected value or code
     */
    public FieldError(String field, String message) {
        this(field, message, null, null);
    }

    /**
     * Creates a FieldError without code
     */
    public FieldError(String field, String message, Object rejectedValue) {
        this(field, message, rejectedValue, null);
    }
}