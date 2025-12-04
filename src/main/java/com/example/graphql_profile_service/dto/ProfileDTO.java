package com.example.graphql_profile_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para perfil de usuario
 * Coincide con ProfileResponse de user-service
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileDTO {
    private Integer profileId;
    private String name; // Nombre del usuario
    private ProfileLocationDTO location; // LocationDTO renombrado a ProfileLocationDTO
}
