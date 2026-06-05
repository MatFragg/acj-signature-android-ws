package com.acj.acjsignature.mobile.androidws.service;

import com.acj.acjsignature.mobile.androidws.model.User;

/**
 * Servicio de OTP.
 * Proporciona métodos para generar, enviar y validar códigos OTP.
 * Los OTPs se almacenan hasheados (BCrypt) en la base de datos.
 */
public interface OtpService {

    /**
     * Genera un nuevo código OTP, lo hashea, lo persiste y lo envía al email del usuario.
     *
     * @param user Usuario que recibirá el OTP
     */
    void generateAndSendOtp(User user);

    /**
     * Valida el código OTP proporcionado contra el hash almacenado.
     * Si el OTP no coincide, incrementa el contador de intentos fallidos.
     * Si coincide, retorna la entidad User (sin limpiar el OTP).
     *
     * @param email Email del usuario
     * @param otp   Código OTP en claro (6 dígitos)
     * @return User validada
     * @throws com.acj.acjsignature.mobile.androidws.exception.BusinessException
     *         Si el OTP ha expirado, se han excedido los intentos, o el código no coincide
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
