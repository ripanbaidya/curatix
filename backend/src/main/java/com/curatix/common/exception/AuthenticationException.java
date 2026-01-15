package com.curatix.common.exception;

import com.curatix.common.constant.ErrorCode;

/**
 * Exception for authentication failures, Use for {@code 401}
 * errors (login failures, invalid tokens, etc.)
 */
public class AuthenticationException extends CuratixException {

    public AuthenticationException(ErrorCode errorCode) {
        super(errorCode);
    }

    public AuthenticationException(ErrorCode errorCode, String customMessage) {
        super(errorCode, customMessage);
    }

    public AuthenticationException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}