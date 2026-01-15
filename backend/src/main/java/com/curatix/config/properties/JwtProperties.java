package com.curatix.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * Configuration properties for Jwt security.
 *
 * @param issuer       issuer of the token
 * @param secret       secret key for the token
 * @param secretLength minimum length allowed for the secret key (recommended: 64)
 * @param header       header where the token is stored
 * @param prefix       prefix in the header for the token
 * @param accessToken  access token properties
 * @param password     password properties
 */
@ConfigurationProperties(prefix = "security.jwt")
public record JwtProperties(
        String issuer,
        String secret,
        int secretLength,
        String header,
        String prefix,
        AccessToken accessToken,
        Password password
) {
    public record AccessToken(
            long expiration
    ) {
    }

    public record RefreshToken(
            Duration expiration
    ) {
    }

    public record Password(
            int encoderStrength,
            int maxLoginAttempts,
            Duration lockoutDuration
    ) {
    }
}
