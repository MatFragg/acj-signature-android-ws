package com.acj.acjsignature.mobile.androidws.service.impl;

import com.acj.acjsignature.mobile.androidws.dto.request.LoginRequest;
import com.acj.acjsignature.mobile.androidws.dto.request.RegisterRequest;
import com.acj.acjsignature.mobile.androidws.dto.request.ResendOtpRequest;
import com.acj.acjsignature.mobile.androidws.dto.request.VerifyOtpRequest;
import com.acj.acjsignature.mobile.androidws.dto.request.ForgotPasswordRequest;
import com.acj.acjsignature.mobile.androidws.dto.request.ResetPasswordRequest;
import com.acj.acjsignature.mobile.androidws.dto.request.ChangePasswordRequest;
import com.acj.acjsignature.mobile.androidws.dto.response.AuthResponse;
import com.acj.acjsignature.mobile.androidws.dto.response.OtpResponse;
import com.acj.acjsignature.mobile.androidws.dto.response.ApiResponse;
import com.acj.acjsignature.mobile.androidws.exception.BusinessException;
import com.acj.acjsignature.mobile.androidws.mapper.UserMapper;
import com.acj.acjsignature.mobile.androidws.model.Role;
import com.acj.acjsignature.mobile.androidws.model.RoleEnum;
import com.acj.acjsignature.mobile.androidws.model.User;
import com.acj.acjsignature.mobile.androidws.repository.RoleRepository;
import com.acj.acjsignature.mobile.androidws.repository.UserRepository;
import com.acj.acjsignature.mobile.androidws.security.JwtTokenProvider;
import com.acj.acjsignature.mobile.androidws.service.AuthService;
import com.acj.acjsignature.mobile.androidws.service.OtpService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

/**
 * Implementación del servicio de autenticación.
 * Maneja el login, registro y verificación de OTP de usuarios, generando tokens JWT.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final OtpService otpService;

    @Value("${app.jwt.expiration}")
    private long jwtExpirationInMs;

    @Override
    public AuthResponse login(LoginRequest request) {
        log.info("Attempting to login user: {}", request.getEmail());

        try {
            User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException("Usuario no encontrado"));

            // Verificar si el email ha sido validado
            if (!user.getEmailVerified()) {
                log.warn("Login attempt for unverified email: {}", request.getEmail());
                throw new BusinessException("Por favor verifica tu email antes de iniciar sesión");
            }

            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.getEmail(),
                    request.getPassword()
                )
            );

            String token = jwtTokenProvider.generateToken(authentication);

            log.info("User {} logged in successfully", request.getEmail());

            return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .expiresIn(jwtExpirationInMs / 1000) // Convertir a segundos
                .user(userMapper.toUserInfo(user))
                .build();

        } catch (org.springframework.security.core.AuthenticationException ex) {
            log.warn("Login failed for user: {}", request.getEmail());
            throw new BusinessException("Email o contraseña incorrectos");
        }
    }

    @Override
    public OtpResponse register(RegisterRequest request) {
        log.info("Attempting to register user: {}", request.getEmail());

        // Validar que el email no exista
        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("Email already exists: {}", request.getEmail());
            throw new BusinessException("Email ya está registrado");
        }

        // Validar que el DNI no exista
        if (userRepository.existsByDni(request.getDni())) {
            log.warn("DNI already exists: {}", request.getDni());
            throw new BusinessException("DNI ya está registrado");
        }

        // Obtener rol por defecto (ROLE_USER)
        Role userRole = roleRepository.findByName(RoleEnum.ROLE_USER)
            .orElseThrow(() -> {
                log.error("Role ROLE_USER not found in database");
                return new BusinessException("Rol por defecto no encontrado");
            });

        // Crear nuevo usuario (no verificado aún)
        User user = User.builder()
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .dni(request.getDni())
            .firstName(request.getFirstName())
            .lastName(request.getLastName())
            .active(true)
            .emailVerified(false)
            .roles(Collections.singleton(userRole))
            .build();

        User savedUser = userRepository.save(user);
        log.info("User {} registered successfully with id: {}", request.getEmail(), savedUser.getId());

        // Generar y enviar OTP
        otpService.generateAndSendOtp(savedUser);

        long expirySeconds = 60; // OTP válido por 60 segundos
        return OtpResponse.builder()
            .message("Se ha enviado un código OTP a tu correo electrónico. Verifica tu email.")
            .expiresIn(expirySeconds)
            .build();
    }

    @Override
    public AuthResponse verifyOtp(VerifyOtpRequest request) {
        log.info("Attempting to verify OTP for user: {}", request.getEmail());

        // Validar el OTP
        otpService.validateOtp(request.getEmail(), request.getOtp());

        // Obtener usuario
        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new BusinessException("Usuario no encontrado"));

        // Limpiar OTP y marcar email como verificado
        otpService.clearOtp(user);

        log.info("OTP verified and email confirmed for user: {}", request.getEmail());

        // Construir autoridades desde los roles del usuario
        var authorities = user.getRoles().stream()
            .map(role -> new SimpleGrantedAuthority(role.getName().name()))
            .toList();

        // Crear UserDetails para que el JwtTokenProvider pueda castearlo
        org.springframework.security.core.userdetails.UserDetails userDetails =
            org.springframework.security.core.userdetails.User.withUsername(user.getEmail())
                .password(user.getPassword())
                .authorities(authorities)
                .build();

        // Crear Authentication directamente sin validar contraseña
        // Ya hemos validado el OTP, por lo que podemos confiar en la identidad del usuario
        // Pasar autoridades al constructor marca automáticamente el token como autenticado
        Authentication authentication = new UsernamePasswordAuthenticationToken(
            userDetails,
            null,
            authorities
        );

        String token = jwtTokenProvider.generateToken(authentication);

        return AuthResponse.builder()
            .token(token)
            .tokenType("Bearer")
            .expiresIn(jwtExpirationInMs / 1000) // Convertir a segundos
            .user(userMapper.toUserInfo(user))
            .build();
    }

    @Override
    public OtpResponse resendOtp(ResendOtpRequest request) {
        log.info("Attempting to resend OTP for user: {}", request.getEmail());

        // Obtener usuario
        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new BusinessException("Usuario no encontrado"));

        // Verificar que el usuario no este ya verificado
        if (user.getEmailVerified()) {
            log.warn("Resend OTP requested for already verified user: {}", request.getEmail());
            throw new BusinessException("Tu email ya esta verificado. Por favor inicia sesion.");
        }

        // Generar y enviar nuevo OTP
        otpService.generateAndSendOtp(user);

        log.info("OTP resent successfully for user: {}", request.getEmail());

        long expirySeconds = 60; // OTP valido por 60 segundos
        return OtpResponse.builder()
            .message("Se ha enviado un nuevo codigo OTP a tu correo electronico. Verifica tu email.")
            .expiresIn(expirySeconds)
            .build();
    }

    @Override
    public OtpResponse forgotPassword(ForgotPasswordRequest request) {
        log.info("Attempting forgot password for user: {}", request.getEmail());
        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new BusinessException("Usuario no encontrado"));

        otpService.generateAndSendOtp(user);

        return OtpResponse.builder()
            .message("Se ha enviado un código OTP a tu correo electrónico para restablecer la contraseña.")
            .expiresIn(60L)
            .build();
    }

    @Override
    public ApiResponse<String> resetPassword(ResetPasswordRequest request) {
        log.info("Attempting to reset password for user: {}", request.getEmail());
        otpService.validateOtp(request.getEmail(), request.getOtp());

        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new BusinessException("Usuario no encontrado"));

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        otpService.clearOtp(user);
        log.info("Password reset successfully for user: {}", request.getEmail());
        return ApiResponse.success("Contraseña restablecida exitosamente", null);
    }

    @Override
    public ApiResponse<String> changePassword(ChangePasswordRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("Attempting to change password for user: {}", email);

        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new BusinessException("Usuario no autenticado o no encontrado"));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new BusinessException("La contraseña actual es incorrecta");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        log.info("Password changed successfully for user: {}", email);
        return ApiResponse.success("Contraseña cambiada exitosamente", null);
    }

    @Override
    public ApiResponse<String> verifyOtpOnly(VerifyOtpRequest request) {
        log.info("Attempting to verify OTP-only for user: {}", request.getEmail());

        // Validar el OTP
        otpService.validateOtp(request.getEmail(), request.getOtp());

        // Obtener usuario
        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new BusinessException("Usuario no encontrado"));

        // Limpiar solo datos del OTP sin marcar email como verificado
        otpService.clearOtpOnly(user);

        log.info("OTP verified (only) for user: {}", request.getEmail());

        return ApiResponse.success("OTP verificado correctamente", null);
    }
}
