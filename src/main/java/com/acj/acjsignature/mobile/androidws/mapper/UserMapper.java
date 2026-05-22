package com.acj.acjsignature.mobile.androidws.mapper;

import com.acj.acjsignature.mobile.androidws.dto.response.UserResponse;
import com.acj.acjsignature.mobile.androidws.model.User;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

/**
 * Mapper para convertir entre User Entity y UserResponse DTO.
 */
@Component
public class UserMapper {

    /**
     * Convierte User entity a UserResponse DTO
     */
    public UserResponse toResponse(User user) {
        if (user == null) {
            return null;
        }

        return UserResponse.builder()
            .id(user.getId())
            .email(user.getEmail())
            .dni(user.getDni())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .fullName(user.getFullName())
            .active(user.getActive())
            .roles(user.getRoles().stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toSet()))
            .createdAt(user.getCreatedAt())
            .updatedAt(user.getUpdatedAt())
            .build();
    }

    /**
     * Convierte User entity a AuthResponse.UserInfo DTO
     */
    public com.acj.acjsignature.mobile.androidws.dto.response.AuthResponse.UserInfo toUserInfo(User user) {
        if (user == null) {
            return null;
        }

        return com.acj.acjsignature.mobile.androidws.dto.response.AuthResponse.UserInfo.builder()
            .id(user.getId())
            .email(user.getEmail())
            .dni(user.getDni())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .fullName(user.getFullName())
            .active(user.getActive())
            .roles(user.getRoles().stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toSet()))
            .build();
    }
}

