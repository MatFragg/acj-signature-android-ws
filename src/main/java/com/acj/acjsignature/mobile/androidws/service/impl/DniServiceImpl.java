package com.acj.acjsignature.mobile.androidws.service.impl;

import com.acj.acjsignature.mobile.androidws.dto.response.DniResponse;
import com.acj.acjsignature.mobile.androidws.exception.BusinessException;
import com.acj.acjsignature.mobile.androidws.exception.ErrorCode;
import com.acj.acjsignature.mobile.androidws.service.DniService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

/**
 * Implementación del servicio de consulta de DNI.
 * Integra con la API Decolecta RENIEC para obtener datos personales.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DniServiceImpl implements DniService {

    private final RestTemplate restTemplate;
    private final com.acj.acjsignature.mobile.androidws.config.AppProperties appProperties;

    @Override
    public DniResponse consultarDni(String numero) {
        log.info("Consultando DNI: {}", numero);

        String token = appProperties.getReniec().getApiToken();
        if (token == null || token.isBlank()) {
            log.error("Token de RENIEC no configurado");
            throw new BusinessException(ErrorCode.RENIEC_NOT_CONFIGURED,
                "Servicio de RENIEC no disponible: token no configurado");
        }

        try {
            String url = appProperties.getReniec().getApiUrl() + "?numero=" + numero;

            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");
            headers.set("Authorization", "Bearer " + token);

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<DniResponse> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                DniResponse.class
            );

            log.info("Respuesta exitosa de RENIEC para DNI: {}", numero);
            return response.getBody();

        } catch (HttpClientErrorException.BadRequest ex) {
            log.warn("Solicitud invalida a RENIEC para DNI: {}", numero);
            throw new BusinessException(ErrorCode.RENIEC_BAD_REQUEST, "Numero de DNI invalido");

        } catch (HttpClientErrorException.NotFound ex) {
            log.warn("DNI no encontrado en RENIEC: {}", numero);
            throw new BusinessException(ErrorCode.DNI_NOT_FOUND,
                ErrorCode.DNI_NOT_FOUND.getDefaultMessage());

        } catch (HttpClientErrorException.Unauthorized ex) {
            log.error("Token de RENIEC invalido o expirado");
            throw new BusinessException(ErrorCode.RENIEC_UNAUTHORIZED,
                ErrorCode.RENIEC_UNAUTHORIZED.getDefaultMessage());

        } catch (HttpClientErrorException ex) {
            log.error("Error en solicitud a RENIEC: {} {}", ex.getStatusCode(), ex.getMessage());
            throw new BusinessException(ErrorCode.RENIEC_ERROR,
                "Error al consultar RENIEC: " + ex.getStatusCode());

        } catch (Exception ex) {
            log.error("Error inesperado al consultar RENIEC para DNI: {}", numero, ex);
            throw new BusinessException(ErrorCode.RENIEC_ERROR,
                ErrorCode.RENIEC_ERROR.getDefaultMessage());
        }
    }
}
