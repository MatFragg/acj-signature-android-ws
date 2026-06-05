package com.acj.acjsignature.mobile.androidws.exception;

import lombok.Getter;

import java.time.Duration;

/**
 * Limite de tasa excedido. Mapea a HTTP 429.
 */
@Getter
public class RateLimitExceededException extends RuntimeException {

    private final ErrorCode errorCode;
    private final Duration retryAfter;

    public RateLimitExceededException(ErrorCode errorCode, Duration retryAfter) {
        super(errorCode.getDefaultMessage());
        this.errorCode = errorCode;
        this.retryAfter = retryAfter;
    }

    public RateLimitExceededException(ErrorCode errorCode, String message, Duration retryAfter) {
        super(message);
        this.errorCode = errorCode;
        this.retryAfter = retryAfter;
    }
}
