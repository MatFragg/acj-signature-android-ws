package com.acj.acjsignature.mobile.androidws.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Utilidades para acceder a información de seguridad del contexto actual.
 *
 * Proporciona métodos estáticos para obtener el usuario autenticado y verificar roles
 * desde cualquier parte de la aplicación sin necesidad de inyectar el contexto de seguridad.
 */
@Component
@Slf4j
public class SecurityUtils {

    /**
     * Obtiene el nombre de usuario del usuario autenticado actualmente.
     *
     * @return Optional con el nombre de usuario si existe, vacío en caso contrario.
     */
    public static Optional<String> getCurrentUsername() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return Optional.ofNullable(securityContext.getAuthentication())
            .map(authentication -> {
                if (authentication.getPrincipal() instanceof UserDetails) {
                    UserDetails springSecurityUser = (UserDetails) authentication.getPrincipal();
                    return springSecurityUser.getUsername();
                } else if (authentication.getPrincipal() instanceof String) {
                    return (String) authentication.getPrincipal();
                }
                return null;
            });
    }

    /**
     * Obtiene el objeto Authentication del usuario actualmente autenticado.
     *
     * @return Optional con la autenticación si existe, vacío en caso contrario.
     */
    public static Optional<Authentication> getCurrentAuthentication() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication());
    }

    /**
     * Verifica si el usuario autenticado tiene un rol específico.
     *
     * @param role El rol a verificar (ej: "ROLE_ADMIN", "ROLE_USER")
     * @return true si el usuario tiene el rol, false en caso contrario
     */
    public static boolean hasRole(String role) {
        return SecurityContextHolder.getContext()
            .getAuthentication()
            .getAuthorities()
            .stream()
            .anyMatch(authority -> authority.getAuthority().equals(role));
    }

    /**
     * Verifica si el usuario está autenticado.
     *
     * @return true si el usuario está autenticado, false en caso contrario
     */
    public static boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated() &&
               !authentication.getAuthorities().isEmpty();
    }

    /**
     * Obtiene el objeto UserDetails del usuario autenticado.
     *
     * @return Optional con el UserDetails si existe, vacío en caso contrario
     */
    public static Optional<UserDetails> getCurrentUserDetails() {
        return getCurrentAuthentication()
            .filter(auth -> auth.getPrincipal() instanceof UserDetails)
            .map(auth -> (UserDetails) auth.getPrincipal());
    }
}

