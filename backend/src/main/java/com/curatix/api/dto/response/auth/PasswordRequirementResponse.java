package com.curatix.api.dto.response.auth;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Password requirement response
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record PasswordRequirementResponse(
        Integer minLength,
        Integer maxLength,
        Boolean requireUppercase,
        Boolean requireLowercase,
        Boolean requireNumber,
        String description,
        String example
) {
}
