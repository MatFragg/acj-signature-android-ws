package com.acj.acjsignature.mobile.androidws.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para respuesta de consulta de DNI desde RENIEC.
 * Contiene los datos personales extraídos del DNI.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DniResponse {

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("first_last_name")
    private String firstLastName;

    @JsonProperty("second_last_name")
    private String secondLastName;

    @JsonProperty("full_name")
    private String fullName;

    @JsonProperty("document_number")
    private String documentNumber;
}

