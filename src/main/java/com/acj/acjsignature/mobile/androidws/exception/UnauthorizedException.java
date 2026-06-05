package com.acj.acjsignature.mobile.androidws.exception;

/**
 * Credenciales invalidas o acceso no autorizado. Mapea a HTTP 401.
 */
public class UnauthorizedException extends BusinessException {

    public UnauthorizedException(String message) {
        super(ErrorCode.AUTH_INVALID_CREDENTIALS, message);
    }

    public UnauthorizedException(ErrorCode errorCode) {
        super(errorCode);
    }

    public UnauthorizedException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
