package com.acj.acjsignature.mobile.androidws.security;

import com.acj.acjsignature.mobile.androidws.config.AppProperties;
import com.acj.acjsignature.mobile.androidws.dto.response.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Filtro de rate limiting basado en Bucket4j.
 * Aplica límites por IP para los endpoints sensibles de autenticación.
 * Almacenamiento en memoria (un solo nodo); para multi-instancia migrar a Redis.
 */
@Component
@Order(1)
@RequiredArgsConstructor
@Slf4j
public class RateLimitFilter extends OncePerRequestFilter {

    private static final String IP_HEADER = "X-Forwarded-For";
    private static final String AUTH_PATH = "/api/v1/auth/";

    private final AppProperties appProperties;
    private final ObjectMapper objectMapper;

    private final Map<String, Bucket> loginBuckets = new ConcurrentHashMap<>();
    private final Map<String, Bucket> registerBuckets = new ConcurrentHashMap<>();
    private final Map<String, Bucket> otpVerifyBuckets = new ConcurrentHashMap<>();
    private final Map<String, Bucket> forgotBuckets = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();
        String method = request.getMethod();

        if (!"POST".equalsIgnoreCase(method) || !path.startsWith(AUTH_PATH)) {
            filterChain.doFilter(request, response);
            return;
        }

        String ip = resolveClientIp(request);
        String key = ip + "|" + path;
        Bucket bucket = resolveBucket(path, key);

        if (bucket == null) {
            filterChain.doFilter(request, response);
            return;
        }

        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);
        if (probe.isConsumed()) {
            response.setHeader("X-Rate-Limit-Remaining", String.valueOf(probe.getRemainingTokens()));
            filterChain.doFilter(request, response);
        } else {
            long waitSeconds = probe.getNanosToWaitForRefill() / 1_000_000_000L;
            log.warn("Rate limit exceeded for IP {} on {} (retry in {}s)", ip, path, waitSeconds);
            writeRateLimitResponse(response, path, waitSeconds);
        }
    }

    private Bucket resolveBucket(String path, String key) {
        AppProperties.RateLimit cfg = appProperties.getRateLimit();
        if (path.endsWith("/login")) {
            return loginBuckets.computeIfAbsent(key, k -> newBucket(cfg.getLoginPerMinute(), Duration.ofMinutes(1)));
        }
        if (path.endsWith("/register")) {
            return registerBuckets.computeIfAbsent(key, k -> newBucket(cfg.getRegisterPerHour(), Duration.ofHours(1)));
        }
        if (path.endsWith("/verify-otp") || path.endsWith("/verify-otp-only")) {
            return otpVerifyBuckets.computeIfAbsent(key, k -> newBucket(cfg.getOtpVerifyPerMinute(), Duration.ofMinutes(1)));
        }
        if (path.endsWith("/forgot-password")) {
            return forgotBuckets.computeIfAbsent(key, k -> newBucket(cfg.getForgotPasswordPerHour(), Duration.ofHours(1)));
        }
        return null;
    }

    private Bucket newBucket(int capacity, Duration period) {
        Bandwidth limit = Bandwidth.builder()
            .capacity(capacity)
            .refillIntervally(capacity, period)
            .build();
        return Bucket.builder().addLimit(limit).build();
    }

    private String resolveClientIp(HttpServletRequest request) {
        String header = request.getHeader(IP_HEADER);
        if (header != null && !header.isBlank()) {
            return header.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private void writeRateLimitResponse(HttpServletResponse response, String path, long retryAfter) throws IOException {
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setHeader("Retry-After", String.valueOf(Math.max(1, retryAfter)));

        ErrorResponse body = ErrorResponse.builder()
            .status(HttpStatus.TOO_MANY_REQUESTS.value())
            .message("Demasiadas solicitudes. Intenta de nuevo en " + Math.max(1, retryAfter) + " segundos.")
            .error("TooManyRequests")
            .timestamp(LocalDateTime.now())
            .path(path)
            .build();

        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}
