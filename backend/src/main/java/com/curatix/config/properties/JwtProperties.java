package com.curatix.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * Configuration properties for Jwt security.
 *
 * @param issuer       issuer of the token
 *                     // @param secret       secret key for the token
 * @param secretLength minimum length allowed for the secret key (recommended: 64)
 * @param header       header where the token is stored
 * @param prefix       prefix in the header for the token
 * @param accessToken  access token properties
 * @param refreshToken refresh token properties
 * @param password     password properties
 * @param rsa          RSA asymmetric encryption configuration
 */
@ConfigurationProperties(prefix = "security.jwt")
public record JwtProperties(
        String issuer,
        //String secret,
        int secretLength,
        String header,
        String prefix,
        AccessToken accessToken,
        RefreshToken refreshToken,
        Password password,
        Rsa rsa
) {
    /**
     * Access token
     *
     * @param expiration token validity duration
     */
    public record AccessToken(
            Duration expiration
    ) {
    }

    /**
     * Refresh token
     *
     * @param expiration token validity duration
     */
    public record RefreshToken(
            Duration expiration
    ) {
    }

    /**
     * Password properties
     *
     * @param encoderStrength  BCrypt encoder strength (work factor)
     * @param maxLoginAttempts maximum allowed consecutive failed login attempts
     * @param lockoutDuration  account lockout duration after exceeding max attempts
     */
    public record Password(
            int encoderStrength,
            int maxLoginAttempts,
            Duration lockoutDuration
    ) {
    }

    /**
     * RSA asymmetric encryption configuration for JWT signing and verification.
     * <p>The private key is used to sign JWTs, while the public key is used to
     * verify token signatures. Keys must be provided in PEM format.</p>
     *
     * @param algorithm      cryptographic algorithm (e.g. {@code RSA})
     * @param keySize        key size in bits (e.g. 2048 or 4096)
     * @param privateKeyPath classpath or filesystem path to the private key
     * @param publicKeyPath  classpath or filesystem path to the public key
     */
    public record Rsa(
            String algorithm,
            int keySize,
            String privateKeyPath,
            String publicKeyPath
    ) {
    }
}
