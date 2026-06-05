package com.acj.acjsignature.mobile.androidws.exception;

import lombok.Getter;

/**
 * Excepcion base para errores de negocio.
 * Cada excepcion tiene un ErrorCode que define el status HTTP y el mensaje por defecto.
 */
@Getter
public class BusinessException extends RuntimeException {

    private final ErrorCode errorCode;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getDefaultMessage());
        this.errorCode = errorCode;
    }

    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public BusinessException(ErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
}
