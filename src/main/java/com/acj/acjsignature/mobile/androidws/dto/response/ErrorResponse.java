package com.acj.acjsignature.mobile.androidws.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * DTO para respuesta de error estandarizada.
 * Devuelve un codigo de error legible por maquina (ErrorCode.code),
 * un mensaje legible por humanos, status HTTP, path y errores de validacion.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    private String code;

    private int status;

    private String error;

    private String message;

    private String path;

    private LocalDateTime timestamp;

    private Map<String, String> validationErrors;
}
