package com.curatix.api.dto.response.auth;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Login response")
public record LoginResponse(
        TokenResponse token,
        UserResponse user
) {
}
