package com.example.graphql_profile_service.controller;

import java.util.List;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import com.example.graphql_profile_service.dto.*;
import com.example.graphql_profile_service.service.PreferenceService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * GraphQL Query Resolver - LEGACY
 * 
 * DESHABILITADO TEMPORALMENTE - Usar SimplePreferenceResolver
 * 
 * Maneja queries complejas con datos relacionados (favorite_artists,
 * favorite_genres tables)
 * Para preferencias simples (arrays de strings), usar SimplePreferenceResolver
 */
// @Controller // COMENTADO PARA EVITAR CONFLICTOS
@RequiredArgsConstructor
@Slf4j
public class UserPreferenceController_DISABLED {

    private final PreferenceService preferenceService;

    /**
     * Query: userProfile
     * Obtiene el perfil completo del usuario desde user-service
     */
    // @QueryMapping
    @PreAuthorize("isAuthenticated()")
    public UserProfileDTO userProfile(@Argument Integer userId) {
        log.info("🔍 GraphQL Query: userProfile(userId: {})", userId);
        return preferenceService.getUserProfile(userId);
    }

    /**
     * Query: userPreferences
     * Obtiene las preferencias del usuario con listas opcionales
     */
    // @QueryMapping
    @PreAuthorize("isAuthenticated()")
    public UserPreferencesDTO userPreferences(
            @Argument Integer userId,
            @Argument(name = "includeFullLists") Boolean includeFullLists) {

        log.info("🔍 GraphQL Query: userPreferences(userId: {}, includeFullLists: {})",
                userId, includeFullLists);

        boolean includeLists = includeFullLists != null ? includeFullLists : false;
        return preferenceService.getUserPreferences(userId, includeLists);
    }

    /**
     * Query: favoriteArtists
     * Obtiene los artistas favoritos paginados
     */
    // @QueryMapping
    @PreAuthorize("isAuthenticated()")
    public ArtistPageDTO favoriteArtists(
            @Argument Integer userId,
            @Argument Integer page,
            @Argument Integer size) {

        log.info("🔍 GraphQL Query: favoriteArtists(userId: {}, page: {}, size: {})",
                userId, page, size);

        int pageNumber = page != null ? page : 0;
        int pageSize = size != null ? size : 10;

        return preferenceService.getFavoriteArtists(userId, pageNumber, pageSize);
    }

    /**
     * Query: favoriteGenres
     * Obtiene los géneros favoritos paginados
     */
    // @QueryMapping
    @PreAuthorize("isAuthenticated()")
    public GenrePageDTO favoriteGenres(
            @Argument Integer userId,
            @Argument Integer page,
            @Argument Integer size) {

        log.info("🔍 GraphQL Query: favoriteGenres(userId: {}, page: {}, size: {})",
                userId, page, size);

        int pageNumber = page != null ? page : 0;
        int pageSize = size != null ? size : 10;

        return preferenceService.getFavoriteGenres(userId, pageNumber, pageSize);
    }

    /**
     * Query: allMusicGenres
     * Obtiene todos los géneros musicales disponibles
     */
    // @QueryMapping
    public List<MusicGenreDTO> allMusicGenres() {
        log.info("🔍 GraphQL Query: allMusicGenres()");
        return preferenceService.getAllMusicGenres();
    }
}