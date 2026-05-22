package com.acj.acjsignature.mobile.androidws.exception;

/**
 * Excepción para errores de negocio.
 * Se lanza cuando ocurre una violación de una regla de negocio.
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}

