package com.curatix.security.jwt;

import com.curatix.common.constant.ErrorCode;
import com.curatix.config.properties.JwtProperties;
import com.curatix.security.exception.InvalidJwtSecretException;
import com.curatix.security.exception.JwtAuthenticationException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;

@Service
@Slf4j
@RequiredArgsConstructor
public class JwtService {

    private static final String CLAIM_USER_ID = "uid";
    private static final String CLAIM_TOKEN_TYPE = "token_type";
    private static final String TOKEN_TYPE_ACCESS = "access";
    private static final String TOKEN_TYPE_REFRESH = "refresh";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtProperties jwtProperties;
    private SecretKey secretKey;

    /**
     * Initialize the Secret Key
     */
    @PostConstruct
    private void initSecretKey() {
        log.info("Initializing Secret key for the System.");

        String secretKey = jwtProperties.secret();
        if (StringUtils.isBlank(secretKey)) {
            log.warn("Secret key is blank");
            throw new InvalidJwtSecretException(ErrorCode.INVALID_SECRET_KEY);
        }

        byte[] secretBytes;
        try {
            secretBytes = Decoders.BASE64.decode(secretKey);
        } catch (IllegalArgumentException ex) {
            throw new InvalidJwtSecretException(ErrorCode.INVALID_SECRET_KEY);
        }

        if (secretBytes.length < jwtProperties.secretLength()) {
            String errorMessage = "JWT secret is not configured or too short. Minimum required: 64 bytes for HS512";
            log.warn(errorMessage);
            throw new InvalidJwtSecretException(ErrorCode.INVALID_SECRET_KEY, errorMessage);
        }

        this.secretKey = Keys.hmacShaKeyFor(secretBytes);
        log.info("Secret key configured successfully.");
    }

    /* Token Generation */

    /**
     * Generate JWT Access token
     */
    public String generateAccessToken(UUID id, String email) {
        Map<String, Object> claims = Map.of(
                CLAIM_USER_ID, id,
                CLAIM_TOKEN_TYPE, TOKEN_TYPE_ACCESS
        );
        return buildToken(claims, email, getExpirationInSeconds(TOKEN_TYPE_ACCESS));
    }

    /**
     * Generate JWT Refresh token
     */
    public String generateRefreshToken(UUID id, String email) {
        Map<String, Object> claims = Map.of(
                CLAIM_USER_ID, id,
                CLAIM_TOKEN_TYPE, TOKEN_TYPE_REFRESH
        );
        return buildToken(claims, email, getExpirationInSeconds(TOKEN_TYPE_REFRESH));
    }

    /* Token Validation */

    /**
     * Validate JWT token
     */
    public boolean isTokenValid(String token) {
        String jwt = stripBearerPrefix(token);
        if (StringUtils.isBlank(jwt) || !jwt.contains(".")) {
            log.warn("JWT token format is Invalid!");
            return false;
        }

        try {
            Jwts.parser()
                    .verifyWith(secretKey)
                    .requireIssuer(jwtProperties.issuer())
                    .clockSkewSeconds(60) // Allow 60s for server clock differences
                    .build()
                    .parseSignedClaims(jwt)
                    .getPayload();
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("JWT expired: {}. Suggested action: Trigger Refresh Token flow.", e.getMessage());
        } catch (SignatureException e) {
            log.error("CRITICAL: JWT signature mismatch. Possible tampering attempt!");
        } catch (MalformedJwtException | UnsupportedJwtException e) {
            log.error("Invalid JWT structure: {}", e.getMessage());
        } catch (Exception e) {
            log.error("JWT validation failed for unknown reason: {}", e.getMessage());
        }

        return false;
    }

    /**
     * Check whether token is expired
     */
    public boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration()
                .before(new Date());
    }

    /* Claim Extraction */

    /**
     * Generic method to extract any claim from the token
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        return claimResolver.apply(extractClaims(token));
    }

    /**
     * Extract user ID from token
     */
    public UUID extractUserId(String token) {
        Claims claims = extractClaims(token);
        final String userId = claims.get(CLAIM_USER_ID, String.class);

        if (userId == null || StringUtils.isBlank(userId)) {
            log.error("Token missing required user ID claim: {}", CLAIM_USER_ID);
            throw new JwtAuthenticationException(
                    ErrorCode.TOKEN_INVALID, "Token missing required user ID claim"
            );

        }

        try {
            return UUID.fromString(userId);
        } catch (IllegalArgumentException e) {
            log.error("Corrupted UUID in token claim: {}", userId);
            throw new InvalidJwtSecretException(
                    ErrorCode.TOKEN_INVALID, "User identity format is corrupt"
            );
        }
    }

    /**
     * Extract email(username) from token
     */
    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extract token type
     */
    public String extractTokenType(String token) {
        Claims claims = extractClaims(token);
        return claims.get(CLAIM_TOKEN_TYPE, String.class);
    }

    /* Helper functions */

    /**
     * Strips the "Bearer" prefix from the token if present.
     */
    private String stripBearerPrefix(String token) {
        if (token != null && token.startsWith(BEARER_PREFIX)) {
            return token.substring(BEARER_PREFIX.length()).trim();
        }
        return token;
    }

    /**
     * Get expiration time of JWT token (in seconds)
     *
     * @param type token type for which we want to get the expiration
     */
    private long getExpirationInSeconds(String type) {
        if (Objects.equals(type, TOKEN_TYPE_ACCESS)) {
            return jwtProperties.accessToken().expiration().getSeconds();
        } else {
            return jwtProperties.refreshToken().expiration().getSeconds();
        }
    }

    /**
     * Build JWT token (Access + Refresh) from Claims.
     */
    private String buildToken(Map<String, Object> claims, String email, long expInSeconds) {
        Instant now = Instant.now();
        Instant expAt = now.plusSeconds(expInSeconds);

        return Jwts.builder()
                .claims(claims)
                .issuer(jwtProperties.issuer())
                .subject(email)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expAt))
                .signWith(secretKey, Jwts.SIG.HS512)
                .compact();
    }

    /**
     * Extract all claims from the JWT token
     */
    private Claims extractClaims(String token) {
        try {
            String jwt = stripBearerPrefix(token);
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .requireIssuer(jwtProperties.issuer())
                    .clockSkewSeconds(60) // Allow 60s for server clock differences
                    .build()
                    .parseSignedClaims(jwt)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            log.debug("JWT token expired: {}", e.getMessage());
            throw new JwtAuthenticationException(ErrorCode.TOKEN_EXPIRED);
        } catch (JwtException e) {
            log.error("Failed to extract claims: {}", e.getMessage());
            throw new JwtAuthenticationException(ErrorCode.TOKEN_INVALID);
        } catch (Exception e) {
            log.error("Unexpected error parsing JWT: {}", e.getMessage());
            throw new JwtAuthenticationException(ErrorCode.INTERNAL_ERROR);
        }
    }

}
