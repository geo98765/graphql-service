package com.example.graphql_profile_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para ubicación del perfil
 * Coincide con ProfileLocationResponse de user-service
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileLocationDTO {
    private Integer profileLocationId;
    private String municipality;
    private String state;
    private String country;
}
