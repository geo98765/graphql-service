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
 * GraphQL Query Resolver
 * Maneja todas las queries (consultas de lectura) de GraphQL
 * 
 * IMPORTANTE: Este resolver tiene seguridad habilitada mediante Spring Security
 * Todas las queries requieren autenticación HTTP Basic
 */
@Controller
@RequiredArgsConstructor
@Slf4j
public class UserPreferenceController {
    
    private final PreferenceService preferenceService;
    
    /**
     * Query: userProfile
     * Obtiene el perfil completo del usuario desde user-service
     * 
     * Requiere autenticación
     * 
     * Ejemplo GraphQL:
     * query {
     *   userProfile(userId: 1) {
     *     userId
     *     email
     *     name
     *     location {
     *       city
     *       country
     *     }
     *   }
     * }
     */
    @QueryMapping
    @PreAuthorize("isAuthenticated()")
    public UserProfileDTO userProfile(@Argument Integer userId) {
        log.info("🔍 GraphQL Query: userProfile(userId: {})", userId);
        return preferenceService.getUserProfile(userId);
    }
    
    /**
     * Query: userPreferences
     * Obtiene las preferencias del usuario con listas opcionales
     * 
     * Requiere autenticación
     * 
     * Ejemplo GraphQL:
     * query {
     *   userPreferences(userId: 1, includeFullLists: true) {
     *     searchRadius
     *     emailNotifications
     *     favoriteArtists {
     *       name
     *       genres
     *     }
     *     favoriteGenres {
     *       name
     *     }
     *   }
     * }
     */
    @QueryMapping
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
     * 
     * Requiere autenticación
     * 
     * Ejemplo GraphQL:
     * query {
     *   favoriteArtists(userId: 1, page: 0, size: 10) {
     *     content {
     *       name
     *       popularity
     *       imageUrl
     *     }
     *     totalElements
     *     hasNext
     *   }
     * }
     */
    @QueryMapping
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
     * 
     * Requiere autenticación
     * 
     * Ejemplo GraphQL:
     * query {
     *   favoriteGenres(userId: 1, page: 0, size: 10) {
     *     content {
     *       name
     *       description
     *     }
     *     totalElements
     *   }
     * }
     */
    @QueryMapping
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
     * 
     * Esta query es PÚBLICA (no requiere autenticación)
     * para permitir explorar el catálogo de géneros
     * 
     * Ejemplo GraphQL:
     * query {
     *   allMusicGenres {
     *     musicGenreId
     *     name
     *     description
     *   }
     * }
     */
    @QueryMapping
    public List<MusicGenreDTO> allMusicGenres() {
        log.info("🔍 GraphQL Query: allMusicGenres()");
        return preferenceService.getAllMusicGenres();
    }
}