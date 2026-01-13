package com.curatix.common.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Enum representing all possible error types in the Curatix system.
 * <p>Maps to specific HTTP status codes and error categories.</p>
 */
@Getter
@AllArgsConstructor
public enum ErrorType {
    /**
     *
     */
    BAD_REQUEST("Bad Request", 400),
    VALIDATION_ERROR("Validation Failed", 400),
    AUTHENTICATION_ERROR("Authentication Failed", 401),
    AUTHORIZATION_ERROR("Access Denied", 403),
    RESOURCE_NOT_FOUND("Resource Not Found", 404),
    METHOD_NOT_ALLOWED("Method Not Allowed", 405),
    RESOURCE_CONFLICT("Resource Already Exists", 409),
    UNSUPPORTED_MEDIA_TYPE("Unsupported Media Type", 415),
    BUSINESS_LOGIC_ERROR("Business Rule Violation", 422),
    RATE_LIMIT_ERROR("Rate Limit Exceeded", 429),
    INTERNAL_SERVER_ERROR("Internal Server Error", 500),
    SERVICE_UNAVAILABLE("Service Unavailable", 503),
    GATEWAY_TIMEOUT("Gateway Timeout", 504);

    private final String title;
    private final int statusCode;
}