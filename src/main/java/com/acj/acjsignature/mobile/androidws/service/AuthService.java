package com.acj.acjsignature.mobile.androidws.service;

import com.acj.acjsignature.mobile.androidws.dto.request.LoginRequest;
import com.acj.acjsignature.mobile.androidws.dto.request.RegisterRequest;
import com.acj.acjsignature.mobile.androidws.dto.request.ResendOtpRequest;
import com.acj.acjsignature.mobile.androidws.dto.request.VerifyOtpRequest;
import com.acj.acjsignature.mobile.androidws.dto.response.AuthResponse;
import com.acj.acjsignature.mobile.androidws.dto.response.OtpResponse;
import com.acj.acjsignature.mobile.androidws.dto.request.ForgotPasswordRequest;
import com.acj.acjsignature.mobile.androidws.dto.request.ResetPasswordRequest;
import com.acj.acjsignature.mobile.androidws.dto.request.ChangePasswordRequest;
import com.acj.acjsignature.mobile.androidws.dto.response.ApiResponse;

/**
 * Servicio de autenticación.
 *
 * Define los métodos para login, registro y verificación de OTP de usuarios.
 */
public interface AuthService {

    /**
     * Autentica un usuario y genera un token JWT.
     */
    AuthResponse login(LoginRequest request);

    /**
     * Registra un nuevo usuario en el sistema.
     * El usuario recibirá un OTP por email para verificar su cuenta.
     */
    OtpResponse register(RegisterRequest request);

    /**
     * Verifica el codigo OTP enviado al email del usuario durante el registro.
     */
    AuthResponse verifyOtp(VerifyOtpRequest request);

    /**
     * Reenvio de OTP.
     * Util cuando el OTP ha expirado o el usuario no lo recibio.
     * Resetea los intentos fallidos y genera un nuevo OTP.
     */
    OtpResponse resendOtp(ResendOtpRequest request);

    /**
     * Envia un OTP al email para recuperar la contraseña.
     */
    OtpResponse forgotPassword(ForgotPasswordRequest request);

    /**
     * Valida el OTP y cambia la contraseña.
     */
    ApiResponse<String> resetPassword(ResetPasswordRequest request);

    /**
     * Cambia la contraseña del usuario autenticado.
     */
    ApiResponse<String> changePassword(ChangePasswordRequest request);

    /**
     * Verifica un OTP sin crear sesión ni devolver JWT.
     * Debe marcar el OTP como usado para evitar reuso, pero no modificar el estado de verificación de email.
     */
    ApiResponse<String> verifyOtpOnly(VerifyOtpRequest request);
}
