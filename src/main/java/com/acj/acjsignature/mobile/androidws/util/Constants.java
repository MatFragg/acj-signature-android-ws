package com.acj.acjsignature.mobile.androidws.util;

/**
 * Constantes del dominio.
 */
public final class Constants {

    private Constants() {
    }

    /**
     * Patron de contrasena fuerte:
     * al menos una mayuscula, una minuscula y un digito.
     */
    public static final String PASSWORD_PATTERN =
        "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$";

    public static final String DNI_PATTERN = "^\\d{8}$";

    public static final String OTP_PATTERN = "^\\d{6}$";

    public static final int NAME_MIN_LENGTH = 2;
    public static final int NAME_MAX_LENGTH = 100;
    public static final int EMAIL_MIN_LENGTH = 3;
    public static final int EMAIL_MAX_LENGTH = 100;
    public static final int PASSWORD_MAX_LENGTH = 255;
}
