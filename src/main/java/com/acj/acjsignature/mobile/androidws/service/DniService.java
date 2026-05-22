package com.acj.acjsignature.mobile.androidws.service;

import com.acj.acjsignature.mobile.androidws.dto.response.DniResponse;

/**
 * Servicio para consultar datos de DNI en RENIEC.
 *
 * Integración con API Decolecta RENIEC.
 */
public interface DniService {

    /**
     * Consulta información de una persona por número de DNI.
     *
     * @param numero Número de DNI (8 dígitos)
     * @return Datos personales extraídos del DNI
     * @throws com.acj.acjsignature.mobile.androidws.exception.BusinessException
     *         Si el DNI no existe, el token es inválido, o hay error en la API
     */
    DniResponse consultarDni(String numero);
}

