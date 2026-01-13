package com.curatix.common.response;

/**
 * Wrapper for all error responses.
 * Always contains success=false and detailed error information.
 *
 * @param success Always false for error responses
 * @param error   Detailed error information
 */
public record ErrorResponse(
        boolean success,
        ErrorDetail error
) {
    /**
     * Creates an error response with ErrorDetail
     */
    public static ErrorResponse of(ErrorDetail error) {
        return new ErrorResponse(false, error);
    }
}