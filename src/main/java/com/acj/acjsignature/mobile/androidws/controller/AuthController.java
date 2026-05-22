package com.acj.acjsignature.mobile.androidws.controller;

import com.acj.acjsignature.mobile.androidws.dto.request.LoginRequest;
import com.acj.acjsignature.mobile.androidws.dto.request.RegisterRequest;
import com.acj.acjsignature.mobile.androidws.dto.request.ResendOtpRequest;
import com.acj.acjsignature.mobile.androidws.dto.request.VerifyOtpRequest;
import com.acj.acjsignature.mobile.androidws.dto.request.ForgotPasswordRequest;
import com.acj.acjsignature.mobile.androidws.dto.request.ResetPasswordRequest;
import com.acj.acjsignature.mobile.androidws.dto.request.ChangePasswordRequest;
import com.acj.acjsignature.mobile.androidws.dto.response.ApiResponse;
import com.acj.acjsignature.mobile.androidws.dto.response.AuthResponse;
import com.acj.acjsignature.mobile.androidws.dto.response.OtpResponse;
import com.acj.acjsignature.mobile.androidws.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador de Autenticación.
 * Expone los endpoints para login, registro y verificación OTP de usuarios.
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    /**
     * Endpoint para login de usuario.
     *
     * @param request Credenciales del usuario (email, password)
     * @return Token JWT y datos del usuario
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request) {
        log.info("Login request for user: {}", request.getEmail());

        AuthResponse response = authService.login(request);

        return ResponseEntity.ok(
            ApiResponse.success("Login exitoso", response)
        );
    }

    /**
     * Endpoint para registro de usuario.
     * Genera y envía un OTP al email del usuario.
     *
     * @param request Datos para registrar nuevo usuario
     * @return Mensaje indicando que se envió el OTP
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<OtpResponse>> register(
            @Valid @RequestBody RegisterRequest request) {
        log.info("Register request for user: {}", request.getEmail());

        OtpResponse response = authService.register(request);

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Usuario registrado. Verifica tu email para completar el proceso.", response));
    }

    /**
     * Endpoint para verificar el código OTP.
     * Una vez verificado, el usuario podrá iniciar sesión.
     *
     * @param request Email y código OTP
     * @return Token JWT y datos del usuario verificado
     */
    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse<AuthResponse>> verifyOtp(
            @Valid @RequestBody VerifyOtpRequest request) {
        log.info("OTP verification request for user: {}", request.getEmail());

        AuthResponse response = authService.verifyOtp(request);

        return ResponseEntity.ok(
            ApiResponse.success("Email verificado exitosamente. Bienvenido!", response)
        );
    }

    /**
     * Endpoint público para verificar un OTP sin crear sesión ni devolver JWT.
     * No marca el email como verificado; solo invalida/elimina el OTP para evitar reuso.
     */
    @PostMapping("/verify-otp-only")
    public ResponseEntity<ApiResponse<String>> verifyOtpOnly(
            @Valid @RequestBody VerifyOtpRequest request) {
        log.info("Verify OTP only request for user: {}", request.getEmail());

        ApiResponse<String> response = authService.verifyOtpOnly(request);

        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint para reenviar codigo OTP.
     * Util cuando el OTP ha expirado o no fue recibido.
     *
     * @param request Email del usuario
     * @return Mensaje indicando que se reenvio el OTP
     */
    @PostMapping("/resend-otp")
    public ResponseEntity<ApiResponse<OtpResponse>> resendOtp(
            @Valid @RequestBody ResendOtpRequest request) {
        log.info("Resend OTP request for user: {}", request.getEmail());

        OtpResponse response = authService.resendOtp(request);

        return ResponseEntity.ok(
            ApiResponse.success("Nuevo codigo OTP enviado a tu email", response)
        );
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<OtpResponse>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {
        log.info("Forgot password request for user: {}", request.getEmail());
        OtpResponse response = authService.forgotPassword(request);
        return ResponseEntity.ok(ApiResponse.success("OTP enviado", response));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<String>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {
        log.info("Reset password request for user: {}", request.getEmail());
        ApiResponse<String> response = authService.resetPassword(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<String>> changePassword(
            @Valid @RequestBody ChangePasswordRequest request) {
        log.info("Change password request");
        ApiResponse<String> response = authService.changePassword(request);
        return ResponseEntity.ok(response);
    }
}
