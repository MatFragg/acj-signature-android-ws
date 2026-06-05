package com.acj.acjsignature.mobile.androidws.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
@Validated
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    @Valid
    @NotNull
    private Jwt jwt = new Jwt();

    @Valid
    @NotNull
    private Otp otp = new Otp();

    @Valid
    @NotNull
    private Mail mail = new Mail();

    @Valid
    @NotNull
    private Reniec reniec = new Reniec();

    @Valid
    @NotNull
    private Cors cors = new Cors();

    @Valid
    @NotNull
    private RateLimit rateLimit = new RateLimit();

    @Valid
    @NotNull
    private Bootstrap bootstrap = new Bootstrap();

    @Data
    public static class Jwt {
        @NotBlank(message = "app.jwt.secret es obligatorio")
        @Size(min = 64, max = 1024, message = "app.jwt.secret debe tener al menos 64 caracteres (HS512)")
        private String secret;

        @Positive(message = "app.jwt.expiration debe ser positivo")
        private long expiration = 86400000L;

        @Positive(message = "app.jwt.refresh-expiration debe ser positivo")
        private long refreshExpiration = 604800000L;

        @NotBlank(message = "app.jwt.issuer es obligatorio")
        private String issuer = "acj-signature-api";
    }

    @Data
    public static class Otp {
        @Positive(message = "app.otp.expiry-seconds debe ser positivo")
        private long expirySeconds = 300L;

        @Positive(message = "app.otp.max-failed-attempts debe ser positivo")
        private int maxFailedAttempts = 3;

        @Positive(message = "app.otp.length debe ser positivo")
        private int length = 6;
    }

    @Data
    public static class Mail {
        @NotBlank(message = "app.mail.from es obligatorio")
        private String from;

        @NotBlank(message = "app.mail.otp-subject es obligatorio")
        private String otpSubject = "Codigo de Verificacion - ACJ Signature";

        @NotBlank(message = "app.mail.brand-name es obligatorio")
        private String brandName = "ACJ Signature";

        @NotBlank(message = "app.mail.base-url es obligatorio")
        private String baseUrl = "http://localhost:8067";

        @NotBlank(message = "app.mail.help-url es obligatorio")
        private String helpUrl = "http://localhost:8067/help";

        @NotBlank(message = "app.mail.privacy-url es obligatorio")
        private String privacyUrl = "http://localhost:8067/privacy";

        @NotBlank(message = "app.mail.terms-url es obligatorio")
        private String termsUrl = "http://localhost:8067/terms";
    }

    @Data
    public static class Reniec {
        @NotBlank(message = "app.reniec.api-url es obligatorio")
        private String apiUrl = "https://api.decolecta.com/v1/reniec/dni";

        @NotBlank(message = "app.reniec.api-token es obligatorio")
        private String apiToken;

        @Positive(message = "app.reniec.connect-timeout-ms debe ser positivo")
        private int connectTimeoutMs = 5000;

        @Positive(message = "app.reniec.read-timeout-ms debe ser positivo")
        private int readTimeoutMs = 5000;
    }

    @Data
    public static class Cors {
        @NotEmpty(message = "app.cors.allowed-origins no puede estar vacio")
        private List<@NotBlank String> allowedOrigins = new ArrayList<>();

        @NotEmpty(message = "app.cors.allowed-methods no puede estar vacio")
        private List<@NotBlank String> allowedMethods = Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS");

        @NotEmpty(message = "app.cors.allowed-headers no puede estar vacio")
        private List<@NotBlank String> allowedHeaders = List.of("*");

        private boolean allowCredentials = false;
    }

    @Data
    public static class RateLimit {
        @Positive
        private int loginPerMinute = 5;

        @Positive
        private int registerPerHour = 3;

        @Positive
        private int otpVerifyPerMinute = 5;

        @Positive
        private int forgotPasswordPerHour = 3;
    }

    @Data
    public static class Bootstrap {
        @NotBlank(message = "app.bootstrap.admin-email es obligatorio")
        private String adminEmail = "admin@test.com";

        @NotBlank(message = "app.bootstrap.admin-password es obligatorio")
        private String adminPassword = "Admin123!";

        @NotBlank(message = "app.bootstrap.user-email es obligatorio")
        private String userEmail = "user@test.com";

        @NotBlank(message = "app.bootstrap.user-password es obligatorio")
        private String userPassword = "User123!";
    }
}
