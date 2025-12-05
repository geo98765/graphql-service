package com.example.graphql_profile_service.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import com.example.graphql_profile_service.model.MusicGenre;
import com.example.graphql_profile_service.model.Profile;
import com.example.graphql_profile_service.model.UserPreference;
import com.example.graphql_profile_service.repository.MusicGenreRepository;
import com.example.graphql_profile_service.repository.ProfileRepository;
import com.example.graphql_profile_service.repository.UserPreferenceRepository;
import com.example.graphql_profile_service.service.SecurityUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Simple GraphQL Resolver for Preferences
 * Resolvers simplificados que coinciden con schema.graphqls
 */
@Controller
@RequiredArgsConstructor
@Slf4j
public class SimplePreferenceResolver {

    private final UserPreferenceRepository preferenceRepository;
    private final ProfileRepository profileRepository;
    private final MusicGenreRepository genreRepository;
    private final SecurityUtils securityUtils;

    // ============================================
    // QUERIES
    // ============================================

    /**
     * Query: getUserPreferences
     * Obtiene las preferencias de un usuario
     */
    @QueryMapping
    @PreAuthorize("isAuthenticated()")
    public PreferenceDTO getUserPreferences(@Argument Integer userId) {
        log.info("🔍 GraphQL Query: getUserPreferences(userId: {})", userId);

        // Validar permisos
        securityUtils.validateUserOwnership(userId);

        // Buscar perfil
        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Profile not found for user: " + userId));

        // Buscar preferencias
        UserPreference pref = preferenceRepository
                .findByProfileProfileId(profile.getProfileId())
                .orElse(null);

        if (pref == null) {
            log.warn("No preferences found for userId: {}", userId);
            return null;
        }

        // Mapear a DTO
        return mapToPreferenceDTO(pref, userId);
    }

    /**
     * Query: getAllGenres
     * Obtiene todos los géneros musicales (público)
     */
    @QueryMapping
    public List<GenreDTO> getAllGenres() {
        log.info("🔍 GraphQL Query: getAllGenres()");

        return genreRepository.findAll().stream()
                .map(this::mapToGenreDTO)
                .toList();
    }

    /**
     * Query: searchGenres
     * Busca géneros por nombre (público)
     */
    @QueryMapping
    public List<GenreDTO> searchGenres(@Argument String name) {
        log.info("🔍 GraphQL Query: searchGenres(name: {})", name);

        return genreRepository.findByNameContainingIgnoreCase(name).stream()
                .map(this::mapToGenreDTO)
                .toList();
    }

    // ============================================
    // MUTATIONS
    // ============================================

    /**
     * Mutation: createPreference
     * Crea preferencias para un usuario
     */
    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    public PreferenceDTO createPreference(
            @Argument Integer userId,
            @Argument PreferenceInputDTO input) {
        log.info("✏️ GraphQL Mutation: createPreference(userId: {})", userId);

        // Validar permisos
        securityUtils.validateUserOwnership(userId);

        // Buscar perfil
        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Profile not found for user: " + userId));

        // Verificar si ya existe
        if (preferenceRepository.findByProfileProfileId(profile.getProfileId()).isPresent()) {
            throw new RuntimeException("Preferences already exist for user: " + userId);
        }

        // Crear preferencias
        UserPreference pref = UserPreference.builder()
                .profile(profile)
                .searchRadius(50.0) // default
                .emailNotifications(true) // default
                .favoriteGenres(input.getFavoriteGenres() != null ? String.join(",", input.getFavoriteGenres()) : "")
                .favoriteCities(input.getFavoriteCities() != null ? String.join(",", input.getFavoriteCities()) : "")
                .favoriteArtists(
                        input.getFavoriteArtists() != null ? String.join(",", input.getFavoriteArtists()) : "")
                .build();

        pref = preferenceRepository.save(pref);
        log.info("✅ Preferences created for userId: {}", userId);

        return mapToPreferenceDTO(pref, userId);
    }

    /**
     * Mutation: updatePreference
     * Actualiza preferencias de un usuario
     */
    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    public PreferenceDTO updatePreference(
            @Argument Integer userId,
            @Argument PreferenceInputDTO input) {
        log.info("✏️ GraphQL Mutation: updatePreference(userId: {})", userId);

        // Validar permisos
        securityUtils.validateUserOwnership(userId);

        // Buscar perfil
        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Profile not found for user: " + userId));

        // Buscar preferencias
        UserPreference pref = preferenceRepository
                .findByProfileProfileId(profile.getProfileId())
                .orElseThrow(() -> new RuntimeException("Preferences not found for user: " + userId));

        // Actualizar campos si están presentes
        if (input.getFavoriteGenres() != null) {
            pref.setFavoriteGenres(String.join(",", input.getFavoriteGenres()));
        }
        if (input.getFavoriteCities() != null) {
            pref.setFavoriteCities(String.join(",", input.getFavoriteCities()));
        }
        if (input.getFavoriteArtists() != null) {
            pref.setFavoriteArtists(String.join(",", input.getFavoriteArtists()));
        }

        pref = preferenceRepository.save(pref);
        log.info("✅ Preferences updated for userId: {}", userId);

        return mapToPreferenceDTO(pref, userId);
    }

    /**
     * Mutation: deletePreference
     * Elimina preferencias de un usuario
     */
    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    public Boolean deletePreference(@Argument Integer userId) {
        log.info("🗑️ GraphQL Mutation: deletePreference(userId: {})", userId);

        // Validar permisos
        securityUtils.validateUserOwnership(userId);

        // Buscar perfil
        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Profile not found for user: " + userId));

        // Buscar y eliminar preferencias
        UserPreference pref = preferenceRepository
                .findByProfileProfileId(profile.getProfileId())
                .orElseThrow(() -> new RuntimeException("Preferences not found for user: " + userId));

        preferenceRepository.delete(pref);
        log.info("✅ Preferences deleted for userId: {}", userId);

        return true;
    }

    // ============================================
    // HELPER METHODS
    // ============================================

    private PreferenceDTO mapToPreferenceDTO(UserPreference pref, Integer userId) {
        return PreferenceDTO.builder()
                .id(pref.getUserPreferenceId())
                .userId(userId)
                .favoriteGenres(splitString(pref.getFavoriteGenres()))
                .favoriteCities(splitString(pref.getFavoriteCities()))
                .favoriteArtists(splitString(pref.getFavoriteArtists()))
                .createdAt(pref.getCreatedAt() != null ? pref.getCreatedAt().toString() : null)
                .updatedAt(pref.getUpdatedAt() != null ? pref.getUpdatedAt().toString() : null)
                .build();
    }

    private GenreDTO mapToGenreDTO(MusicGenre genre) {
        return GenreDTO.builder()
                .id(genre.getMusicGenreId())
                .name(genre.getName())
                .description(genre.getDescription())
                .build();
    }

    private List<String> splitString(String str) {
        if (str == null || str.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return List.of(str.split(","));
    }

    // ============================================
    // DTOs INTERNOS
    // ============================================

    @lombok.Data
    @lombok.Builder
    public static class PreferenceDTO {
        private Integer id;
        private Integer userId;
        private List<String> favoriteGenres;
        private List<String> favoriteCities;
        private List<String> favoriteArtists;
        private String createdAt;
        private String updatedAt;
    }

    @lombok.Data
    @lombok.Builder
    public static class GenreDTO {
        private Integer id;
        private String name;
        private String description;
    }

    @lombok.Data
    public static class PreferenceInputDTO {
        private List<String> favoriteGenres;
        private List<String> favoriteCities;
        private List<String> favoriteArtists;
    }
}
