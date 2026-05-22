package com.acj.acjsignature.mobile.androidws.controller;

import com.acj.acjsignature.mobile.androidws.dto.request.DniRequest;
import com.acj.acjsignature.mobile.androidws.dto.response.ApiResponse;
import com.acj.acjsignature.mobile.androidws.dto.response.DniResponse;
import com.acj.acjsignature.mobile.androidws.service.DniService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador para consulta de datos de DNI.
 * Endpoint público (sin autenticación) para autocompletar
 * datos personales durante el registro de usuarios.
 */
@RestController
@RequestMapping("/api/v1/public/dni")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "DNI", description = "Endpoints para consulta de datos de DNI (sin autenticación)")
public class DniController {

    private final DniService dniService;

    /**
     * Consulta datos personales por número de DNI.
     * Este endpoint está disponible sin autenticación para permitir
     * que el frontend autocomplete campos durante el registro.
     *
     * @param dniRequest DTO con número de DNI (8 dígitos)
     * @return Datos personales: nombres, apellidos y nombre completo
     */
    @PostMapping("/consultar")
    @Operation(summary = "Consultar datos de DNI",
        description = "Obtiene información personal de RENIEC usando el número de DNI. " +
                      "Endpoint público sin autenticación para autocompletar datos en registro.",
        tags = {"DNI"})
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Datos de DNI obtenidos exitosamente",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "DNI inválido (formato incorrecto)",
            content = @Content(mediaType = "application/json")
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "DNI no encontrado en RENIEC",
            content = @Content(mediaType = "application/json")
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "500",
            description = "Error al consultar servicio RENIEC",
            content = @Content(mediaType = "application/json")
        )
    })
    public ResponseEntity<ApiResponse<DniResponse>> consultarDni(
            @Valid @RequestBody DniRequest dniRequest) {
        log.info("Consulta de DNI recibida: {}", dniRequest.getNumero());

        DniResponse response = dniService.consultarDni(dniRequest.getNumero());

        return ResponseEntity.ok(
            ApiResponse.success("Datos de DNI obtenidos exitosamente", response)
        );
    }

    /**
     * Alternativa GET para consultar DNI directamente por parámetro.
     *
     * @param numero Número de DNI (8 dígitos)
     * @return Datos personales
     */
    @GetMapping
    @Operation(summary = "Consultar datos de DNI (GET)",
        description = "Alternativa GET para consultar DNI usando parámetro query",
        tags = {"DNI"})
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Datos de DNI obtenidos exitosamente"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "DNI inválido"
        )
    })
    public ResponseEntity<ApiResponse<DniResponse>> consultarDniGet(
            @Parameter(description = "Número de DNI (8 dígitos)", required = true, example = "46027897")
            @RequestParam(name = "numero") String numero) {
        log.info("Consulta GET de DNI recibida: {}", numero);

        DniRequest request = DniRequest.builder()
            .numero(numero)
            .build();

        // Validar manualmente ya que no estamos usando @Valid en parámetro
        if (numero == null || !numero.matches("^[0-9]{8}$")) {
            throw new jakarta.validation.ConstraintViolationException(
                "El DNI debe contener exactamente 8 dígitos",
                null
            );
        }

        DniResponse response = dniService.consultarDni(numero);

        return ResponseEntity.ok(
            ApiResponse.success("Datos de DNI obtenidos exitosamente", response)
        );
    }
}


