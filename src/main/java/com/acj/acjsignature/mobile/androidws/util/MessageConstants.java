package com.acj.acjsignature.mobile.androidws.util;

/**
 * Mensajes de validacion estandar (espanol).
 * Centralizados para evitar duplicacion y mezclas de idiomas.
 */
public final class MessageConstants {

    private MessageConstants() {
    }

    // Email
    public static final String EMAIL_REQUIRED = "El email es obligatorio";
    public static final String EMAIL_INVALID = "El formato del email es invalido";

    // Password
    public static final String PASSWORD_REQUIRED = "La contrasena es obligatoria";
    public static final String PASSWORD_MIN_LENGTH = "La contrasena debe tener al menos 6 caracteres";
    public static final String PASSWORD_MAX_LENGTH = "La contrasena no puede exceder 255 caracteres";
    public static final String PASSWORD_PATTERN_MESSAGE =
        "La contrasena debe contener al menos una mayuscula, una minuscula y un digito";

    // DNI
    public static final String DNI_REQUIRED = "El DNI es obligatorio";
    public static final String DNI_PATTERN = "El DNI debe contener exactamente 8 digitos";

    // OTP
    public static final String OTP_REQUIRED = "El codigo OTP es obligatorio";
    public static final String OTP_PATTERN = "El codigo OTP debe tener exactamente 6 digitos";

    // Name
    public static final String FIRST_NAME_REQUIRED = "El nombre es obligatorio";
    public static final String FIRST_NAME_SIZE = "El nombre debe tener entre 2 y 100 caracteres";
    public static final String LAST_NAME_REQUIRED = "El apellido es obligatorio";
    public static final String LAST_NAME_SIZE = "El apellido debe tener entre 2 y 100 caracteres";

    // Old password (change-password)
    public static final String OLD_PASSWORD_REQUIRED = "La contrasena actual es obligatoria";
    public static final String NEW_PASSWORD_REQUIRED = "La nueva contrasena es obligatoria";

    // New password
    public static final String NEW_PASSWORD_MIN_LENGTH = "La nueva contrasena debe tener al menos 6 caracteres";

    // Auth
    public static final String INVALID_CREDENTIALS = "Credenciales invalidas";

    // RENIEC
    public static final String RENIEC_NOT_CONFIGURED_TOKEN = "Servicio de RENIEC no disponible: token no configurado";
    public static final String RENIEC_INVALID_DNI = "Numero de DNI invalido";
    public static final String RENIEC_GENERIC_ERROR = "Error al consultar RENIEC: ";
}
