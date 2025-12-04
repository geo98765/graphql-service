package com.example.graphql_profile_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

// ============================================
// USER PROFILE DTOs
// ============================================

/**
 * DTO para información completa del perfil de usuario
 * Se obtiene desde user-service via REST
 * DEBE coincidir con UserResponse de user-service
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDTO {
    private Integer userId;
    private String email;
    private String userType; // CAMBIADO: era "name", ahora "userType" para coincidir con UserResponse

    // Campos adicionales para autenticación (solo para uso interno)
    // NO se exponen en GraphQL, solo se usan para validar credenciales
    private String password;
    private Set<String> roles;

    // ProfileResponse contiene name, location, etc
    private ProfileDTO profile;
    private AccountStatusDTO accountStatus;
    private String createdAt;
    private String updatedAt;
}