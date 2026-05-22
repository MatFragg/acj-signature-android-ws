package com.acj.acjsignature.mobile.androidws.service.impl;

import com.acj.acjsignature.mobile.androidws.exception.BusinessException;
import com.acj.acjsignature.mobile.androidws.model.User;
import com.acj.acjsignature.mobile.androidws.repository.UserRepository;
import com.acj.acjsignature.mobile.androidws.service.EmailService;
import com.acj.acjsignature.mobile.androidws.service.OtpService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;

/**
 * Implementación del servicio de OTP.
 * Maneja la generación, envío y validación de códigos OTP.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OtpServiceImpl implements OtpService {

    private final EmailService emailService;
    private final UserRepository userRepository;

    @Value("${app.otp.expiry-seconds:60}")
    private long otpExpirySeconds;

    @Value("${app.otp.max-failed-attempts:3}")
    private int maxFailedAttempts;

    @Override
    public void generateAndSendOtp(User user) {
        // Generar código OTP de 6 dígitos
        String otp = String.format("%06d", new Random().nextInt(1000000));
        LocalDateTime expiryTime = LocalDateTime.now().plusSeconds(otpExpirySeconds);

        // Actualizar usuario con OTP y tiempo de expiración
        user.setOtpCode(otp);
        user.setOtpExpiryTime(expiryTime);
        user.setOtpFailedAttempts(0);
        userRepository.save(user);

        log.info("OTP generated for user: {}", user.getEmail());

        // Enviar OTP por email
        emailService.sendOtpEmail(user.getEmail(), user.getFullName(), otp);
    }

    @Override
    public boolean validateOtp(String email, String otp) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new BusinessException("Usuario no encontrado"));

        // Verificar si el OTP ha expirado
        if (isOtpExpired(user)) {
            log.warn("OTP expired for user: {}", email);
            throw new BusinessException("El código OTP ha expirado. Solicita uno nuevo.");
        }

        // Verificar si los intentos fallidos han excedido el límite
        if (user.getOtpFailedAttempts() >= maxFailedAttempts) {
            log.warn("Max OTP failed attempts exceeded for user: {}", email);
            throw new BusinessException("Demasiados intentos fallidos. Solicita un nuevo código OTP.");
        }

        // Validar el código OTP
        if (!otp.equals(user.getOtpCode())) {
            user.setOtpFailedAttempts(user.getOtpFailedAttempts() + 1);
            userRepository.save(user);
            log.warn("Invalid OTP for user: {}. Attempts: {}", email, user.getOtpFailedAttempts());
            throw new BusinessException("Código OTP incorrecto. Intenta nuevamente.");
        }

        log.info("OTP validated successfully for user: {}", email);
        return true;
    }

    @Override
    public boolean isOtpExpired(User user) {
        if (user.getOtpExpiryTime() == null) {
            return true;
        }
        return LocalDateTime.now().isAfter(user.getOtpExpiryTime());
    }

    @Override
    public void clearOtp(User user) {
        user.setOtpCode(null);
        user.setOtpExpiryTime(null);
        user.setOtpFailedAttempts(0);
        user.setEmailVerified(true);
        userRepository.save(user);
        log.info("OTP cleared for user and email marked verified: {}", user.getEmail());
    }

    @Override
    public void clearOtpOnly(User user) {
        user.setOtpCode(null);
        user.setOtpExpiryTime(null);
        user.setOtpFailedAttempts(0);
        // NO cambiar emailVerified aquí
        userRepository.save(user);
        log.info("OTP cleared for user without changing emailVerified: {}", user.getEmail());
    }

    @Override
    public void incrementFailedAttempts(User user) {
        user.setOtpFailedAttempts(user.getOtpFailedAttempts() + 1);
        userRepository.save(user);
    }

    @Override
    public long getOtpExpiryTimeRemaining(User user) {
        if (user.getOtpExpiryTime() == null) {
            return 0;
        }

        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(user.getOtpExpiryTime())) {
            return 0;
        }

        return java.time.temporal.ChronoUnit.SECONDS.between(now, user.getOtpExpiryTime());
    }
}
