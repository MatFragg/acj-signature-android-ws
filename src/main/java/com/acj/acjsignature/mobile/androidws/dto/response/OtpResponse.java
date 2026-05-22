package com.acj.acjsignature.mobile.androidws.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para respuesta de OTP.
 * Indica si el OTP se ha enviado exitosamente.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OtpResponse {
    private String message;
    private Long expiresIn; // Tiempo en segundos
}

