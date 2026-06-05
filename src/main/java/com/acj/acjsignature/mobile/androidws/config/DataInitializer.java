package com.acj.acjsignature.mobile.androidws.config;

import com.acj.acjsignature.mobile.androidws.model.Role;
import com.acj.acjsignature.mobile.androidws.model.RoleEnum;
import com.acj.acjsignature.mobile.androidws.model.User;
import com.acj.acjsignature.mobile.androidws.repository.RoleRepository;
import com.acj.acjsignature.mobile.androidws.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;

/**
 * Inicializador de datos para el perfil DEV.
 * Crea los roles por defecto y dos cuentas de prueba (admin/user) si no existen.
 * SOLO se activa con el perfil "dev" para evitar cuentas de backdoor en produccion.
 */
@Configuration
@Profile("dev")
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AppProperties appProperties;

    @Bean
    public CommandLineRunner initializeData() {
        return args -> {
            log.warn("[DEV ONLY] Inicializando base de datos con roles y cuentas de prueba...");

            initializeRoles();
            initializeTestUsers();

            log.warn("[DEV ONLY] Inicializacion de datos de desarrollo completa. " +
                "NO usar este perfil en produccion.");
        };
    }

    private void initializeRoles() {
        for (RoleEnum roleEnum : RoleEnum.values()) {
            if (!roleRepository.existsByName(roleEnum)) {
                Role role = Role.builder()
                    .name(roleEnum)
                    .description(roleEnum.getDescripcion())
                    .build();
                roleRepository.save(role);
                log.info("[DEV] Created role: {}", roleEnum);
            }
        }
    }

    private void initializeTestUsers() {
        AppProperties.Bootstrap bootstrap = appProperties.getBootstrap();

        if (!userRepository.existsByEmail(bootstrap.getAdminEmail())) {
            Role adminRole = roleRepository.findByName(RoleEnum.ROLE_ADMIN)
                .orElseThrow(() -> new IllegalStateException("Admin role not found"));

            User adminUser = User.builder()
                .email(bootstrap.getAdminEmail())
                .password(passwordEncoder.encode(bootstrap.getAdminPassword()))
                .dni("12345678")
                .firstName("Admin")
                .lastName("User")
                .active(true)
                .emailVerified(true)
                .roles(Collections.singleton(adminRole))
                .build();

            userRepository.save(adminUser);
            log.warn("[DEV] Created test admin user: {} (cambia la contrasena al primer login)",
                bootstrap.getAdminEmail());
        }

        if (!userRepository.existsByEmail(bootstrap.getUserEmail())) {
            Role userRole = roleRepository.findByName(RoleEnum.ROLE_USER)
                .orElseThrow(() -> new IllegalStateException("User role not found"));

            User testUser = User.builder()
                .email(bootstrap.getUserEmail())
                .password(passwordEncoder.encode(bootstrap.getUserPassword()))
                .dni("87654321")
                .firstName("Test")
                .lastName("User")
                .active(true)
                .emailVerified(true)
                .roles(Collections.singleton(userRole))
                .build();

            userRepository.save(testUser);
            log.warn("[DEV] Created test user: {} (cambia la contrasena al primer login)",
                bootstrap.getUserEmail());
        }
    }
}
