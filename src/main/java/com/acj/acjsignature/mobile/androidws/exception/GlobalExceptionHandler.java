package com.acj.acjsignature.mobile.androidws.exception;

import com.acj.acjsignature.mobile.androidws.dto.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Manejador global de excepciones.
 * Mapea excepciones tipadas a ErrorResponse con codigos estandar y status HTTP correctos.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(
            BusinessException ex,
            WebRequest request) {
        ErrorCode ec = ex.getErrorCode();
        log.warn("Business exception [{}]: {}", ec.getCode(), ex.getMessage());

        ErrorResponse body = ErrorResponse.builder()
            .code(ec.getCode())
            .status(ec.getStatus().value())
            .error(ec.getStatus().getReasonPhrase())
            .message(ex.getMessage())
            .timestamp(LocalDateTime.now())
            .path(extractPath(request))
            .build();

        return ResponseEntity.status(ec.getStatus()).body(body);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedException(
            UnauthorizedException ex,
            WebRequest request) {
        return handleBusinessException(ex, request);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(
            ResourceNotFoundException ex,
            WebRequest request) {
        return handleBusinessException(ex, request);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequestException(
            BadRequestException ex,
            WebRequest request) {
        return handleBusinessException(ex, request);
    }

    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<ErrorResponse> handleRateLimitExceededException(
            RateLimitExceededException ex,
            WebRequest request) {
        ErrorCode ec = ex.getErrorCode();
        log.warn("Rate limit exceeded: {}", ex.getMessage());

        ErrorResponse body = ErrorResponse.builder()
            .code(ec.getCode())
            .status(ec.getStatus().value())
            .error(ec.getStatus().getReasonPhrase())
            .message(ex.getMessage())
            .timestamp(LocalDateTime.now())
            .path(extractPath(request))
            .build();

        return ResponseEntity.status(ec.getStatus())
            .header("Retry-After", String.valueOf(Math.max(1, ex.getRetryAfter().getSeconds())))
            .body(body);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUsernameNotFoundException(
            UsernameNotFoundException ex,
            WebRequest request) {
        log.warn("User not found: {}", ex.getMessage());

        ErrorResponse body = ErrorResponse.builder()
            .code(ErrorCode.AUTH_INVALID_CREDENTIALS.getCode())
            .status(HttpStatus.UNAUTHORIZED.value())
            .error(HttpStatus.UNAUTHORIZED.getReasonPhrase())
            .message(ErrorCode.AUTH_INVALID_CREDENTIALS.getDefaultMessage())
            .timestamp(LocalDateTime.now())
            .path(extractPath(request))
            .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentialsException(
            BadCredentialsException ex,
            WebRequest request) {
        log.warn("Bad credentials: {}", ex.getMessage());

        ErrorResponse body = ErrorResponse.builder()
            .code(ErrorCode.AUTH_INVALID_CREDENTIALS.getCode())
            .status(HttpStatus.UNAUTHORIZED.value())
            .error(HttpStatus.UNAUTHORIZED.getReasonPhrase())
            .message(ErrorCode.AUTH_INVALID_CREDENTIALS.getDefaultMessage())
            .timestamp(LocalDateTime.now())
            .path(extractPath(request))
            .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(
            AuthenticationException ex,
            WebRequest request) {
        log.warn("Authentication failure: {}", ex.getMessage());

        ErrorResponse body = ErrorResponse.builder()
            .code(ErrorCode.AUTH_INVALID_CREDENTIALS.getCode())
            .status(HttpStatus.UNAUTHORIZED.value())
            .error(HttpStatus.UNAUTHORIZED.getReasonPhrase())
            .message(ErrorCode.AUTH_INVALID_CREDENTIALS.getDefaultMessage())
            .timestamp(LocalDateTime.now())
            .path(extractPath(request))
            .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(
            AccessDeniedException ex,
            WebRequest request) {
        log.warn("Access denied: {}", ex.getMessage());

        ErrorResponse body = ErrorResponse.builder()
            .code(ErrorCode.FORBIDDEN.getCode())
            .status(HttpStatus.FORBIDDEN.value())
            .error(HttpStatus.FORBIDDEN.getReasonPhrase())
            .message(ErrorCode.FORBIDDEN.getDefaultMessage())
            .timestamp(LocalDateTime.now())
            .path(extractPath(request))
            .build();

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex,
            WebRequest request) {
        log.warn("Validation error: {}", ex.getMessage());

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
            errors.put(error.getField(), error.getDefaultMessage())
        );

        ErrorResponse body = ErrorResponse.builder()
            .code(ErrorCode.VALIDATION_ERROR.getCode())
            .status(HttpStatus.BAD_REQUEST.value())
            .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
            .message(ErrorCode.VALIDATION_ERROR.getDefaultMessage())
            .validationErrors(errors)
            .timestamp(LocalDateTime.now())
            .path(extractPath(request))
            .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, MissingServletRequestParameterException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ErrorResponse> handleMalformedRequest(
            Exception ex,
            WebRequest request) {
        log.warn("Malformed request: {}", ex.getMessage());

        ErrorResponse body = ErrorResponse.builder()
            .code(ErrorCode.BAD_REQUEST.getCode())
            .status(HttpStatus.BAD_REQUEST.value())
            .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
            .message("Cuerpo o parametros de la solicitud invalidos")
            .timestamp(LocalDateTime.now())
            .path(extractPath(request))
            .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(
            Exception ex,
            WebRequest request) {
        log.error("Unexpected error", ex);

        ErrorResponse body = ErrorResponse.builder()
            .code(ErrorCode.INTERNAL_ERROR.getCode())
            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
            .message(ErrorCode.INTERNAL_ERROR.getDefaultMessage())
            .timestamp(LocalDateTime.now())
            .path(extractPath(request))
            .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    private String extractPath(WebRequest request) {
        return request.getDescription(false).replace("uri=", "");
    }
}
