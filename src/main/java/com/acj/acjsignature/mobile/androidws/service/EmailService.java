package com.acj.acjsignature.mobile.androidws.service;

/**
 * Servicio de Email.
 * Proporciona métodos para enviar emails.
 */
public interface EmailService {

    /**
     * Envía un email con código OTP.
     *
     * @param email Email del destinatario
     * @param name Nombre del destinatario
     * @param otp Código OTP de 6 dígitos
     */
    void sendOtpEmail(String email, String name, String otp);
}

