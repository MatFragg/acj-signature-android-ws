package com.acj.acjsignature.mobile.androidws.security.annotation;

import java.lang.annotation.*;

/**
 * Anotación para restringir el acceso a un método basado en roles.
 *
 * Ejemplo:
 * @HasAnyRole({"ROLE_ADMIN", "ROLE_SUPERADMIN"})
 * public void deleteUser() { ... }
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface HasAnyRole {
    String[] value() default {};
}

