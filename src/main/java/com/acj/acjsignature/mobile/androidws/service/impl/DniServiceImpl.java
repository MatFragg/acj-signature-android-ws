package com.acj.acjsignature.mobile.androidws.service.impl;

import com.acj.acjsignature.mobile.androidws.dto.response.DniResponse;
import com.acj.acjsignature.mobile.androidws.exception.BusinessException;
import com.acj.acjsignature.mobile.androidws.service.DniService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${app.reniec.api-url:https://api.decolecta.com/v1/reniec/dni}")
    private String reniecApiUrl;

    @Value("${app.reniec.api-token:sk_14760.35ybuCqfqMkbuwxk4mScYsGmG3TPKorR}")
    private String reniecApiToken;

    @Override
    public DniResponse consultarDni(String numero) {
        log.info("Consultando DNI: {}", numero);

        // Validar que el token esté configurado
        if (reniecApiToken == null || reniecApiToken.trim().isEmpty()) {
            log.error("Token de RENIEC no configurado");
            throw new BusinessException("Servicio de RENIEC no disponible: token no configurado");
        }

        try {
            // Construir URL con parámetro de DNI
            String url = reniecApiUrl + "?numero=" + numero;

            // Preparar headers
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");
            headers.set("Authorization", "Bearer " + reniecApiToken);

            HttpEntity<String> entity = new HttpEntity<>(headers);

            // Realizar petición GET a RENIEC
            ResponseEntity<DniResponse> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                DniResponse.class
            );

            log.info("Respuesta exitosa de RENIEC para DNI: {}", numero);
            return response.getBody();

        } catch (HttpClientErrorException.BadRequest ex) {
            log.warn("Solicitud inválida a RENIEC para DNI: {}", numero);
            throw new BusinessException("Número de DNI inválido");

        } catch (HttpClientErrorException.NotFound ex) {
            log.warn("DNI no encontrado en RENIEC: {}", numero);
            throw new BusinessException("DNI no encontrado en RENIEC");

        } catch (HttpClientErrorException.Unauthorized ex) {
            log.error("Token de RENIEC inválido o expirado");
            throw new BusinessException("Error de autenticación con RENIEC");

        } catch (HttpClientErrorException ex) {
            log.error("Error en solicitud a RENIEC: {} {}", ex.getStatusCode(), ex.getMessage());
            throw new BusinessException("Error al consultar RENIEC: " + ex.getStatusCode());

        } catch (Exception ex) {
            log.error("Error inesperado al consultar RENIEC para DNI: {}", numero, ex);
            throw new BusinessException("Error al consultar servicio RENIEC");
        }
    }
}

