package com.acj.acjsignature.mobile.androidws.model;

/**
 * Enumeración de roles disponibles en el sistema.
 * - ROLE_USER: Usuario estándar con acceso limitado
 * - ROLE_ADMIN: Administrador con acceso ampliado
 * - ROLE_SUPERADMIN: Súper administrador con acceso total
 */
public enum RoleEnum {
    ROLE_USER("Usuario"),
    ROLE_ADMIN("Administrador"),
    ROLE_SUPERADMIN("Súper Administrador");

    private final String descripcion;

    RoleEnum(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}

