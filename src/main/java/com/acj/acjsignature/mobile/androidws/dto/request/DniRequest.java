package com.acj.acjsignature.mobile.androidws.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para solicitud de consulta de DNI.
 * Contiene el número de DNI a consultar en la API RENIEC.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DniRequest {

    @NotBlank(message = "El número de DNI es requerido")
    @Pattern(regexp = "^[0-9]{8}$", message = "El DNI debe contener exactamente 8 dígitos")
    private String numero;
}

