package com.acj.acjsignature.mobile.androidws.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * DTO para respuesta de autenticación (login y registro).
 * Contiene el JWT token, información del usuario autenticado y sus roles.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {

    private String token;
    private String tokenType;
    private Long expiresIn;
    private UserInfo user;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UserInfo {
        private Long id;
        private String email;
        private String dni;
        private String firstName;
        private String lastName;
        private String fullName;
        private Boolean active;
        private Set<String> roles;
    }
}

