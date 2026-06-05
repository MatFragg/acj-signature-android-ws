package com.acj.acjsignature.mobile.androidws.controller;

import com.acj.acjsignature.mobile.androidws.dto.request.DniRequest;
import com.acj.acjsignature.mobile.androidws.dto.response.ApiResponse;
import com.acj.acjsignature.mobile.androidws.dto.response.DniResponse;
import com.acj.acjsignature.mobile.androidws.service.DniService;
import com.acj.acjsignature.mobile.androidws.util.Constants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador para consulta de datos de DNI.
 * Endpoint publico (sin autenticacion) para autocompletar
 * datos personales durante el registro de usuarios.
 */
@RestController
@RequestMapping("/api/v1/public/dni")
@RequiredArgsConstructor
@Slf4j
@Validated
@Tag(name = "DNI", description = "Endpoints para consulta de datos de DNI (sin autenticacion)")
public class DniController {

    private final DniService dniService;

    @PostMapping("/consultar")
    @Operation(summary = "Consultar datos de DNI",
        description = "Obtiene informacion personal de RENIEC usando el numero de DNI. " +
                      "Endpoint publico sin autenticacion para autocompletar datos en registro.",
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
            description = "DNI invalido (formato incorrecto)",
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

    @GetMapping
    @Operation(summary = "Consultar datos de DNI (GET)",
        description = "Alternativa GET para consultar DNI usando parametro query",
        tags = {"DNI"})
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Datos de DNI obtenidos exitosamente"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "DNI invalido"
        )
    })
    public ResponseEntity<ApiResponse<DniResponse>> consultarDniGet(
            @Parameter(description = "Numero de DNI (8 digitos)", required = true, example = "46027897")
            @RequestParam(name = "numero")
            @Pattern(regexp = Constants.DNI_PATTERN, message = "El DNI debe contener exactamente 8 digitos")
            String numero) {
        log.info("Consulta GET de DNI recibida: {}", numero);

        DniResponse response = dniService.consultarDni(numero);

        return ResponseEntity.ok(
            ApiResponse.success("Datos de DNI obtenidos exitosamente", response)
        );
    }
}
