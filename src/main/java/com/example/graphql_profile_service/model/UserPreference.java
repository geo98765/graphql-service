package com.example.graphql_profile_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "user_preferences")
public class UserPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_preference_id", nullable = false)
    private Integer userPreferenceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false)
    private Profile profile;

    @Column(name = "search_radius")
    private Double searchRadius;

    @Column(name = "email_notifications")
    private Boolean emailNotifications;

    // ===== CAMPOS DE FAVORITOS (almacenados como strings separados por comas)
    // =====

    @Column(name = "favorite_genres", columnDefinition = "TEXT")
    private String favoriteGenres; // "Rock,Metal,Jazz"

    @Column(name = "favorite_cities", columnDefinition = "TEXT")
    private String favoriteCities; // "New York,London,Tokyo"

    @Column(name = "favorite_artists", columnDefinition = "TEXT")
    private String favoriteArtists; // "Metallica,Queen,Beatles"

    // ===== TIMESTAMPS =====

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}