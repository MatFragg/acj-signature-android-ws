package com.acj.acjsignature.mobile.androidws.service.impl;

import com.acj.acjsignature.mobile.androidws.config.AppProperties;
import com.acj.acjsignature.mobile.androidws.dto.response.OtpResponse;
import com.acj.acjsignature.mobile.androidws.exception.BusinessException;
import com.acj.acjsignature.mobile.androidws.exception.ErrorCode;
import com.acj.acjsignature.mobile.androidws.exception.ResourceNotFoundException;
import com.acj.acjsignature.mobile.androidws.model.User;
import com.acj.acjsignature.mobile.androidws.repository.UserRepository;
import com.acj.acjsignature.mobile.androidws.service.EmailService;
import com.acj.acjsignature.mobile.androidws.service.OtpService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

/**
 * Implementación del servicio de OTP.
 * Maneja la generación, envío y validación de códigos OTP.
 * El OTP se almacena hasheado con BCrypt; solo viaja en claro en el email.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OtpServiceImpl implements OtpService {

    private static final SecureRandom RANDOM = new SecureRandom();

    private final EmailService emailService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AppProperties appProperties;

    @Override
    public OtpResponse generateAndSendOtp(User user, String message) {
        String otp = generateNumericOtp(appProperties.getOtp().getLength());
        String otpHash = passwordEncoder.encode(otp);
        long expirySeconds = appProperties.getOtp().getExpirySeconds();
        LocalDateTime expiryTime = LocalDateTime.now()
            .plusSeconds(expirySeconds);

        user.setOtpCode(otpHash);
        user.setOtpExpiryTime(expiryTime);
        user.setOtpFailedAttempts(0);
        userRepository.save(user);

        log.info("OTP generated for user: {}", user.getEmail());

        emailService.sendOtpEmail(user.getEmail(), user.getFullName(), otp);

        return OtpResponse.builder()
            .message(message)
            .expiresIn(expirySeconds)
            .build();
    }

    @Override
    public User validateOtp(String email, String otp) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.USER_NOT_FOUND,
                "Usuario no encontrado"));

        if (isOtpExpired(user)) {
            log.warn("OTP expired for user: {}", email);
            throw new BusinessException(ErrorCode.OTP_EXPIRED,
                ErrorCode.OTP_EXPIRED.getDefaultMessage());
        }

        int maxAttempts = appProperties.getOtp().getMaxFailedAttempts();
        if (user.getOtpFailedAttempts() != null
            && user.getOtpFailedAttempts() >= maxAttempts) {
            log.warn("Max OTP failed attempts exceeded for user: {}", email);
            throw new BusinessException(ErrorCode.OTP_MAX_ATTEMPTS,
                ErrorCode.OTP_MAX_ATTEMPTS.getDefaultMessage());
        }

        String storedHash = user.getOtpCode();
        if (storedHash == null || !passwordEncoder.matches(otp, storedHash)) {
            int attempts = (user.getOtpFailedAttempts() == null ? 0 : user.getOtpFailedAttempts()) + 1;
            user.setOtpFailedAttempts(attempts);
            userRepository.save(user);
            log.warn("Invalid OTP for user: {}. Attempts: {}", email, attempts);
            throw new BusinessException(ErrorCode.OTP_INVALID,
                ErrorCode.OTP_INVALID.getDefaultMessage());
        }

        log.info("OTP validated successfully for user: {}", email);
        return user;
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
        userRepository.save(user);
        log.info("OTP cleared for user without changing emailVerified: {}", user.getEmail());
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

    private String generateNumericOtp(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(RANDOM.nextInt(10));
        }
        return sb.toString();
    }
}
