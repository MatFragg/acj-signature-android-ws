package com.acj.acjsignature.mobile.androidws.service.impl;

import com.acj.acjsignature.mobile.androidws.exception.BusinessException;
import com.acj.acjsignature.mobile.androidws.exception.ErrorCode;
import com.acj.acjsignature.mobile.androidws.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.internet.MimeMessage;

import java.io.IOException;
import java.time.Year;
import java.util.Base64;

/**
 * Implementación del servicio de Email.
 * Utiliza JavaMailSender para enviar emails.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${app.mail.from}")
    private String fromEmail;

    @Value("${app.mail.otp-subject}")
    private String otpSubject;

    @Value("${app.mail.brand-name:ACJ Signature}")
    private String brandName;

    @Value("${app.mail.base-url:http://localhost:8067}")
    private String baseUrl;

    @Value("${app.mail.help-url:http://localhost:8067/help}")
    private String helpUrl;

    @Value("${app.mail.privacy-url:http://localhost:8067/privacy}")
    private String privacyUrl;

    @Value("${app.mail.terms-url:http://localhost:8067/terms}")
    private String termsUrl;

    @Value("${app.otp.expiry-seconds:60}")
    private long otpExpirySeconds;

    @Override
    public void sendOtpEmail(String email, String name, String otp) {
        try {
            Context context = new Context();
            context.setVariable("nombre", name != null && !name.isBlank() ? name : "usuario");
            context.setVariable("codigo", otp);
            context.setVariable("minutos", Math.max(1, otpExpirySeconds / 60));
            context.setVariable("anio", Year.now().getValue());
            context.setVariable("email", email);
            context.setVariable("url_base", baseUrl);
            context.setVariable("url_ayuda", helpUrl);
            context.setVariable("url_privacidad", privacyUrl);
            context.setVariable("url_terminos", termsUrl);
            context.setVariable("brand_name", brandName);

            String htmlContent = templateEngine.process("email", context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(email);
            helper.setSubject(otpSubject);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("OTP email sent successfully to: {}", email);
        } catch (Exception ex) {
            log.error("Error sending OTP email to {}: {}", email, ex.getMessage(), ex);
            throw new BusinessException(ErrorCode.OTP_EMAIL_SEND_FAILED,
                ErrorCode.OTP_EMAIL_SEND_FAILED.getDefaultMessage(), ex);
        }
    }

    private String loadLogoAsBase64() {
        try {
            ClassPathResource resource = new ClassPathResource("static/images/signature.jpg");
            byte[] imageBytes = resource.getInputStream().readAllBytes();
            String base64 = Base64.getEncoder().encodeToString(imageBytes);
            return "data:image/jpeg;base64," + base64;
        } catch (IOException e) {
            return ""; // fallback silencioso
        }
    }
}
