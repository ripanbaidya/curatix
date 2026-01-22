package com.curatix.api.dto.response.auth;

/**
 * Response containing authentication tokens.
 *
 * @param accessToken  JWT access token for authenticated requests.
 * @param refreshToken JWT refresh token for obtaining new access tokens.
 * @param tokenType    Type of the token, typically "Bearer".
 * @param expiresIn    Expiration time of the access token in seconds.
 */
public record TokenResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        Long expiresIn
) {
}
