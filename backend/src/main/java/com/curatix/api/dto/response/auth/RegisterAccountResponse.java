package com.curatix.api.dto.response.auth;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Register account response")
public record RegisterAccountResponse(
        TokenResponse token,
        UserResponse user
) {
}
