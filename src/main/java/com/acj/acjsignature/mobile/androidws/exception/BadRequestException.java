package com.acj.acjsignature.mobile.androidws.exception;

/**
 * Solicitud invalida. Mapea a HTTP 400.
 */
public class BadRequestException extends BusinessException {

    public BadRequestException(ErrorCode errorCode) {
        super(errorCode);
    }

    public BadRequestException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
