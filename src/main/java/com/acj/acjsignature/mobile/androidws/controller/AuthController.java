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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Auth", description = "Endpoints de autenticacion y gestion de usuarios")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesion", description = "Autentica al usuario y devuelve un token JWT")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request) {
        log.info("Login request for user: {}", request.getEmail());

        AuthResponse response = authService.login(request);

        return ResponseEntity.ok(
            ApiResponse.success("Login exitoso", response)
        );
    }

    @PostMapping("/register")
    @Operation(summary = "Registrar usuario", description = "Crea un nuevo usuario y envia OTP de verificacion")
    public ResponseEntity<ApiResponse<OtpResponse>> register(
            @Valid @RequestBody RegisterRequest request) {
        log.info("Register request for user: {}", request.getEmail());

        OtpResponse response = authService.register(request);

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Usuario registrado. Verifica tu email para completar el proceso.", response));
    }

    @PostMapping("/verify-otp")
    @Operation(summary = "Verificar OTP y autenticar", description = "Valida OTP y devuelve token JWT")
    public ResponseEntity<ApiResponse<AuthResponse>> verifyOtp(
            @Valid @RequestBody VerifyOtpRequest request) {
        log.info("OTP verification request for user: {}", request.getEmail());

        AuthResponse response = authService.verifyOtp(request);

        return ResponseEntity.ok(
            ApiResponse.success("Email verificado exitosamente. Bienvenido!", response)
        );
    }

    @PostMapping("/verify-otp-only")
    @Operation(summary = "Solo verificar OTP", description = "Valida OTP sin generar sesion JWT")
    public ResponseEntity<ApiResponse<String>> verifyOtpOnly(
            @Valid @RequestBody VerifyOtpRequest request) {
        log.info("Verify OTP only request for user: {}", request.getEmail());

        ApiResponse<String> response = authService.verifyOtpOnly(request);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/resend-otp")
    @Operation(summary = "Reenviar OTP", description = "Genera y envia un nuevo OTP al correo")
    public ResponseEntity<ApiResponse<OtpResponse>> resendOtp(
            @Valid @RequestBody ResendOtpRequest request) {
        log.info("Resend OTP request for user: {}", request.getEmail());

        OtpResponse response = authService.resendOtp(request);

        return ResponseEntity.ok(
            ApiResponse.success("Nuevo codigo OTP enviado a tu email", response)
        );
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Olvide contrasena", description = "Envia OTP para restablecer contrasena")
    public ResponseEntity<ApiResponse<OtpResponse>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {
        log.info("Forgot password request for user: {}", request.getEmail());
        OtpResponse response = authService.forgotPassword(request);
        return ResponseEntity.ok(ApiResponse.success("OTP enviado", response));
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Restablecer contrasena", description = "Cambia la contrasena verificando el OTP")
    public ResponseEntity<ApiResponse<String>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {
        log.info("Reset password request for user: {}", request.getEmail());
        ApiResponse<String> response = authService.resetPassword(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/change-password")
    @Operation(summary = "Cambiar contrasena", description = "Cambia la contrasena del usuario autenticado")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<String>> changePassword(
            @Valid @RequestBody ChangePasswordRequest request) {
        log.info("Change password request");
        ApiResponse<String> response = authService.changePassword(request);
        return ResponseEntity.ok(response);
    }
}
