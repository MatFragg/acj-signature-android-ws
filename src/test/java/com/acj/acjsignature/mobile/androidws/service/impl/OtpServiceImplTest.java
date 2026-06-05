package com.acj.acjsignature.mobile.androidws.service.impl;

import com.acj.acjsignature.mobile.androidws.config.AppProperties;
import com.acj.acjsignature.mobile.androidws.dto.response.OtpResponse;
import com.acj.acjsignature.mobile.androidws.exception.BusinessException;
import com.acj.acjsignature.mobile.androidws.model.User;
import com.acj.acjsignature.mobile.androidws.repository.UserRepository;
import com.acj.acjsignature.mobile.androidws.service.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OtpServiceImplTest {

    @Mock
    private EmailService emailService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AppProperties appProperties;

    @InjectMocks
    private OtpServiceImpl otpService;

    private User testUser;
    private AppProperties.Otp otpProps;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .email("test@acj.com")
                .firstName("Test")
                .lastName("User")
                .build();

        otpProps = new AppProperties.Otp();
        otpProps.setLength(6);
        otpProps.setExpirySeconds(300L);
        otpProps.setMaxFailedAttempts(3);

        lenient().when(appProperties.getOtp()).thenReturn(otpProps);
    }

    @Test
    void generateAndSendOtp_ShouldUpdateUserAndSendEmail() {
        when(passwordEncoder.encode(anyString())).thenReturn("hashedOtp");

        OtpResponse response = otpService.generateAndSendOtp(testUser, "OTP sent");

        assertNotNull(response);
        assertEquals("OTP sent", response.getMessage());
        assertEquals(300L, response.getExpiresIn());

        verify(userRepository).save(testUser);
        verify(emailService).sendOtpEmail(eq("test@acj.com"), anyString(), anyString());
        assertEquals("hashedOtp", testUser.getOtpCode());
        assertNotNull(testUser.getOtpExpiryTime());
        assertEquals(0, testUser.getOtpFailedAttempts());
    }

    @Test
    void validateOtp_Success() {
        testUser.setOtpCode("hashedOtp");
        testUser.setOtpExpiryTime(LocalDateTime.now().plusMinutes(5));
        testUser.setOtpFailedAttempts(0);

        when(userRepository.findByEmail("test@acj.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("123456", "hashedOtp")).thenReturn(true);

        User validatedUser = otpService.validateOtp("test@acj.com", "123456");

        assertNotNull(validatedUser);
        assertEquals("test@acj.com", validatedUser.getEmail());
    }

    @Test
    void validateOtp_Expired() {
        testUser.setOtpExpiryTime(LocalDateTime.now().minusMinutes(5)); // Expired

        when(userRepository.findByEmail("test@acj.com")).thenReturn(Optional.of(testUser));

        assertThrows(BusinessException.class, () -> otpService.validateOtp("test@acj.com", "123456"));
    }

    @Test
    void validateOtp_InvalidCode() {
        testUser.setOtpCode("hashedOtp");
        testUser.setOtpExpiryTime(LocalDateTime.now().plusMinutes(5));
        testUser.setOtpFailedAttempts(0);

        when(userRepository.findByEmail("test@acj.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrong", "hashedOtp")).thenReturn(false);

        assertThrows(BusinessException.class, () -> otpService.validateOtp("test@acj.com", "wrong"));

        verify(userRepository).save(testUser); // should save failed attempts
        assertEquals(1, testUser.getOtpFailedAttempts());
    }
}
