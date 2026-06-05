package com.acj.acjsignature.mobile.androidws.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Codigos de error estandar de la API.
 * Cada codigo tiene un status HTTP y un mensaje por defecto.
 */
@Getter
public enum ErrorCode {

    AUTH_INVALID_CREDENTIALS("AUTH_INVALID_CREDENTIALS", HttpStatus.UNAUTHORIZED, "Credenciales invalidas"),
    UNAUTHORIZED("UNAUTHORIZED", HttpStatus.UNAUTHORIZED, "No autorizado"),
    FORBIDDEN("FORBIDDEN", HttpStatus.FORBIDDEN, "Acceso denegado"),

    USER_NOT_FOUND("USER_NOT_FOUND", HttpStatus.NOT_FOUND, "Usuario no encontrado"),
    USER_ALREADY_EXISTS("USER_ALREADY_EXISTS", HttpStatus.CONFLICT, "El usuario ya esta registrado"),
    DNI_ALREADY_EXISTS("DNI_ALREADY_EXISTS", HttpStatus.CONFLICT, "El DNI ya esta registrado"),
    EMAIL_ALREADY_VERIFIED("EMAIL_ALREADY_VERIFIED", HttpStatus.BAD_REQUEST, "El email ya esta verificado"),

    OTP_INVALID("OTP_INVALID", HttpStatus.BAD_REQUEST, "Codigo OTP incorrecto"),
    OTP_EXPIRED("OTP_EXPIRED", HttpStatus.BAD_REQUEST, "El codigo OTP ha expirado. Solicita uno nuevo"),
    OTP_MAX_ATTEMPTS("OTP_MAX_ATTEMPTS", HttpStatus.TOO_MANY_REQUESTS, "Demasiados intentos fallidos. Solicita un nuevo codigo OTP"),

    DNI_NOT_FOUND("DNI_NOT_FOUND", HttpStatus.NOT_FOUND, "DNI no encontrado en RENIEC"),
    RENIEC_UNAUTHORIZED("RENIEC_UNAUTHORIZED", HttpStatus.BAD_GATEWAY, "Error de autenticacion con RENIEC"),
    RENIEC_BAD_REQUEST("RENIEC_BAD_REQUEST", HttpStatus.BAD_REQUEST, "Solicitud invalida a RENIEC"),
    RENIEC_ERROR("RENIEC_ERROR", HttpStatus.BAD_GATEWAY, "Error al consultar RENIEC"),
    RENIEC_NOT_CONFIGURED("RENIEC_NOT_CONFIGURED", HttpStatus.SERVICE_UNAVAILABLE, "Servicio de RENIEC no disponible"),

    VALIDATION_ERROR("VALIDATION_ERROR", HttpStatus.BAD_REQUEST, "Errores de validacion"),
    BAD_REQUEST("BAD_REQUEST", HttpStatus.BAD_REQUEST, "Solicitud invalida"),
    RATE_LIMIT_EXCEEDED("RATE_LIMIT_EXCEEDED", HttpStatus.TOO_MANY_REQUESTS, "Demasiadas solicitudes. Intenta mas tarde"),

    ROLE_NOT_FOUND("ROLE_NOT_FOUND", HttpStatus.INTERNAL_SERVER_ERROR, "Rol no encontrado en la base de datos"),
    CURRENT_PASSWORD_INVALID("CURRENT_PASSWORD_INVALID", HttpStatus.UNAUTHORIZED, "La contrasena actual es incorrecta"),
    OTP_EMAIL_SEND_FAILED("OTP_EMAIL_SEND_FAILED", HttpStatus.INTERNAL_SERVER_ERROR, "No se pudo enviar el email con el codigo OTP"),

    INTERNAL_ERROR("INTERNAL_ERROR", HttpStatus.INTERNAL_SERVER_ERROR, "Error interno del servidor");

    private final String code;
    private final HttpStatus status;
    private final String defaultMessage;

    ErrorCode(String code, HttpStatus status, String defaultMessage) {
        this.code = code;
        this.status = status;
        this.defaultMessage = defaultMessage;
    }
}
