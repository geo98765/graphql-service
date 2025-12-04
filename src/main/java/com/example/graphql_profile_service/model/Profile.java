package com.example.graphql_profile_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

// ============================================
// PROFILE - Solo referencia para FK
// ============================================

/**
 * Profile entity - Simplified version
 * Solo necesitamos el ID para las relaciones FK
 * La información completa viene de user-service
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "profiles")
public class Profile {
    
    @Id
    @Column(name = "profile_id", nullable = false)
    private Integer profileId;
    
    @Column(name = "name", length = 100)
    private String name;
    
    @Column(name = "user_id")
    private Integer userId;
}