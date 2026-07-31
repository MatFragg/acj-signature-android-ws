package com.acj.acjsignature.mobile.androidws.config;

import com.acj.acjsignature.mobile.androidws.model.Role;
import com.acj.acjsignature.mobile.androidws.model.RoleEnum;
import com.acj.acjsignature.mobile.androidws.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

/**
 * Inicializador de roles por defecto.
 * Corre en todos los perfiles para garantizar que los roles existan
 * antes de registrar usuarios (independiente del perfil dev).
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class RoleDataInitializer {

    private final RoleRepository roleRepository;

    @Bean
    @Order(1)
    public CommandLineRunner initializeRoles() {
        return args -> {
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
        };
    }
}
