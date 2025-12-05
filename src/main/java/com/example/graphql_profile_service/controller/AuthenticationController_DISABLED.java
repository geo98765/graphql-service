package com.example.graphql_profile_service.controller;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import com.example.graphql_profile_service.dto.*;
import com.example.graphql_profile_service.service.PreferenceService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * GraphQL Mutation Resolver - LEGACY
 * 
 * DESHABILITADO TEMPORALMENTE - Usar SimplePreferenceResolver
 * 
 * Maneja mutations complejas con tablas relacionadas (favorite_artists,
 * favorite_genres)
 * Para preferencias simples (arrays de strings), usar SimplePreferenceResolver
 */
// @Controller // COMENTADO PARA EVITAR CONFLICTOS
@RequiredArgsConstructor
@Slf4j
public class AuthenticationController_DISABLED {

    private final PreferenceService preferenceService;

    /**
     * Mutation: updateUserPreferences
     * Actualiza las preferencias del usuario
     */
    // @MutationMapping
    @PreAuthorize("isAuthenticated()")
    public PreferenceBasicDTO updateUserPreferences(@Argument UpdatePreferencesInput input) {
        log.info("✏️ GraphQL Mutation: updateUserPreferences(userId: {})", input.getUserId());
        return preferenceService.updateUserPreferences(input);
    }

    /**
     * Mutation: addFavoriteArtist
     * Agrega un artista favorito
     */
    // @MutationMapping
    @PreAuthorize("isAuthenticated()")
    public ArtistDTO addFavoriteArtist(
            @Argument Integer userId,
            @Argument String spotifyId) {

        log.info("✏️ GraphQL Mutation: addFavoriteArtist(userId: {}, spotifyId: {})",
                userId, spotifyId);

        return preferenceService.addFavoriteArtist(userId, spotifyId);
    }

    /**
     * Mutation: removeFavoriteArtist
     * Elimina un artista favorito
     */
    // @MutationMapping
    @PreAuthorize("isAuthenticated()")
    public String removeFavoriteArtist(
            @Argument Integer userId,
            @Argument String spotifyId) {

        log.info("✏️ GraphQL Mutation: removeFavoriteArtist(userId: {}, spotifyId: {})",
                userId, spotifyId);

        return preferenceService.removeFavoriteArtist(userId, spotifyId);
    }

    /**
     * Mutation: addFavoriteGenre
     * Agrega un género favorito
     */
    // @MutationMapping
    @PreAuthorize("isAuthenticated()")
    public MusicGenreDTO addFavoriteGenre(
            @Argument Integer userId,
            @Argument Integer genreId,
            @Argument String genreName) {

        log.info("✏️ GraphQL Mutation: addFavoriteGenre(userId: {}, genreId: {}, genreName: {})",
                userId, genreId, genreName);

        return preferenceService.addFavoriteGenre(userId, genreId, genreName);
    }

    /**
     * Mutation: removeFavoriteGenre
     * Elimina un género favorito
     */
    // @MutationMapping
    @PreAuthorize("isAuthenticated()")
    public String removeFavoriteGenre(
            @Argument Integer userId,
            @Argument Integer genreId,
            @Argument String genreName) {

        log.info("✏️ GraphQL Mutation: removeFavoriteGenre(userId: {}, genreId: {}, genreName: {})",
                userId, genreId, genreName);

        return preferenceService.removeFavoriteGenre(userId, genreId, genreName);
    }
}