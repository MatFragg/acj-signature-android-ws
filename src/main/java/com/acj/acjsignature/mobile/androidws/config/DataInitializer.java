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
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;

/**
 * Inicializador de datos para la base de datos.
 * Crea los roles por defecto y un usuario administrador de prueba si no existen.
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner initializeData() {
        return args -> {
            log.info("Initializing database with default roles and users...");

            // Crear roles si no existen
            initializeRoles();

            // Crear usuario admin de prueba si no existe
            initializeTestUsers();

            log.info("Database initialization completed");
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
                log.info("Created role: {}", roleEnum);
            }
        }
    }

    private void initializeTestUsers() {
        // Crear usuario admin de prueba
        if (!userRepository.existsByEmail("admin@test.com")) {
            Role adminRole = roleRepository.findByName(RoleEnum.ROLE_ADMIN)
                .orElseThrow(() -> new RuntimeException("Admin role not found"));

            User adminUser = User.builder()
                .email("admin@test.com")
                .password(passwordEncoder.encode("Admin123"))
                .dni("12345678")
                .firstName("Admin")
                .lastName("User")
                .active(true)
                .roles(Collections.singleton(adminRole))
                .build();

            userRepository.save(adminUser);
            log.info("Created test admin user: admin@test.com");
        }

        // Crear usuario estándar de prueba
        if (!userRepository.existsByEmail("user@test.com")) {
            Role userRole = roleRepository.findByName(RoleEnum.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("User role not found"));

            User testUser = User.builder()
                .email("user@test.com")
                .password(passwordEncoder.encode("User123"))
                .dni("87654321")
                .firstName("Test")
                .lastName("User")
                .active(true)
                .roles(Collections.singleton(userRole))
                .build();

            userRepository.save(testUser);
            log.info("Created test user: user@test.com");
        }
    }
}

