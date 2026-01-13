package com.curatix.common.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.springframework.http.HttpStatus;

import java.time.Instant;

/**
 * Generic wrapper for all successful API responses.
 *
 * @param <T>       Type of the data payload
 * @param success   Always true for successful responses
 * @param status    HTTP status code
 * @param message   Human-readable success message
 * @param data      Response payload (can be null for DELETE operations)
 * @param timestamp When the response was generated (UTC)
 */
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@JsonPropertyOrder(value = {
        "success",
        "status",
        "message",
        "data",
        "timestamp"
})
public record ResponseWrapper<T>(
        boolean success,
        int status,
        String message,
        T data,
        @JsonFormat(shape = JsonFormat.Shape.STRING)
        Instant timestamp
) {
    /**
     * Success response with 200 OK status
     */
    public static <T> ResponseWrapper<T> ok(String message, T data) {
        return new ResponseWrapper<>(true, HttpStatus.OK.value(), message, data, Instant.now());
    }

    /**
     * Success response with 201 CREATED status
     */
    public static <T> ResponseWrapper<T> created(String message, T data) {
        return new ResponseWrapper<>(true, HttpStatus.CREATED.value(), message, data, Instant.now());
    }

    /**
     * Success response with 202 ACCEPTED status (async operation)
     */
    public static <T> ResponseWrapper<T> accepted(String message, T data) {
        return new ResponseWrapper<>(true, HttpStatus.ACCEPTED.value(), message, data, Instant.now());
    }

    /**
     * Success response with null data. (e.g., DELETE)
     */
    public static <T> ResponseWrapper<T> ok(String message) {
        return new ResponseWrapper<>(true, HttpStatus.OK.value(), message, null, Instant.now());
    }

    /**
     * Create a custom status response
     */
    public static <T> ResponseWrapper<T> of(HttpStatus httpStatus, String message, T data) {
        return new ResponseWrapper<>(true, httpStatus.value(), message, data, Instant.now());
    }
}
