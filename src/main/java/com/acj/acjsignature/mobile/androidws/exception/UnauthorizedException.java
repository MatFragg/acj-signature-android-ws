package com.acj.acjsignature.mobile.androidws.exception;

/**
 * Excepcion para credenciales invalidas o acceso no autorizado.
 * Mapea a HTTP 401 Unauthorized.
 */
public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(String message) {
        super(message);
    }

    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }
}
