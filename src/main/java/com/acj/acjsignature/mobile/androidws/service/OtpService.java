package com.acj.acjsignature.mobile.androidws.service;

import com.acj.acjsignature.mobile.androidws.dto.response.OtpResponse;
import com.acj.acjsignature.mobile.androidws.model.User;

/**
 * Servicio de OTP.
 * Proporciona metodos para generar, enviar y validar codigos OTP.
 * Los OTPs se almacenan hasheados (BCrypt) en la base de datos.
 */
public interface OtpService {

    /**
     * Genera un nuevo codigo OTP, lo hashea, lo persiste y lo envia al email del usuario.
     *
     * @param user Usuario que recibira el OTP
     * @param message Mensaje a incluir en la respuesta (segun el flujo: registro, resend, etc.)
     * @return OtpResponse con el tiempo de expiracion en segundos
     */
    OtpResponse generateAndSendOtp(User user, String message);

    /**
     * Valida el codigo OTP proporcionado contra el hash almacenado.
     * Si el OTP no coincide, incrementa el contador de intentos fallidos.
     * Si coincide, retorna la entidad User (sin limpiar el OTP).
     *
     * @param email Email del usuario
     * @param otp   Codigo OTP en claro (6 digitos)
     * @return User validada
     */
    User validateOtp(String email, String otp);

    /**
     * Verifica si el OTP ha expirado para un usuario.
     */
    boolean isOtpExpired(User user);

    /**
     * Limpia los datos de OTP de un usuario y marca el email como verificado.
     * Usado para: registro, verifyOtp (post-verificacion de cuenta).
     */
    void clearOtp(User user);

    /**
     * Limpia los datos de OTP de un usuario SIN marcar el email como verificado.
     * Usado para: verifyOtpOnly, forgot-password / reset-password.
     */
    void clearOtpOnly(User user);

    /**
     * Obtiene el tiempo restante en segundos para que expire el OTP.
     */
    long getOtpExpiryTimeRemaining(User user);
}

