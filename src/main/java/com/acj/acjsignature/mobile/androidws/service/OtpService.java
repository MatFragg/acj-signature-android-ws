package com.acj.acjsignature.mobile.androidws.service;

import com.acj.acjsignature.mobile.androidws.model.User;

/**
 * Servicio de OTP.
 * Proporciona métodos para generar, enviar y validar códigos OTP.
 */
public interface OtpService {

    /**
     * Genera y envía un nuevo código OTP al email del usuario.
     *
     * @param user Usuario que recibirá el OTP
     */
    void generateAndSendOtp(User user);

    /**
     * Valida el código OTP proporcionado.
     *
     * @param email Email del usuario
     * @param otp Código OTP a validar (6 dígitos)
     * @return true si el OTP es válido, false en caso contrario
     */
    boolean validateOtp(String email, String otp);

    /**
     * Verifica si el OTP ha expirado para un usuario.
     *
     * @param user Usuario a verificar
     * @return true si el OTP ha expirado, false en caso contrario
     */
    boolean isOtpExpired(User user);

    /**
     * Limpia los datos de OTP de un usuario (después de validación exitosa).
     * Este método también marca el email como verificado (uso para registro/email verify).
     *
     * @param user Usuario cuyo OTP será limpiado
     */
    void clearOtp(User user);

    /**
     * Limpia los datos de OTP de un usuario sin modificar el estado de verificación de email.
     * Uso: verificación exclusivamente para flujos como "verify-otp-only" o forgot-password.
     *
     * @param user Usuario cuyo OTP será limpiado
     */
    void clearOtpOnly(User user);

    /**
     * Incrementa el contador de intentos fallidos de OTP.
     *
     * @param user Usuario
     */
    void incrementFailedAttempts(User user);

    /**
     * Obtiene el tiempo restante en segundos para que expire el OTP.
     *
     * @param user Usuario
     * @return Segundos restantes, 0 si ya expiró
     */
    long getOtpExpiryTimeRemaining(User user);
}
