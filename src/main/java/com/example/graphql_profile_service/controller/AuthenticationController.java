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
 * GraphQL Mutation Resolver
 * Maneja todas las mutations (operaciones de escritura) de GraphQL
 * 
 * IMPORTANTE: Este resolver tiene seguridad habilitada mediante Spring Security
 * Todas las mutations requieren autenticación HTTP Basic
 */
@Controller
@RequiredArgsConstructor
@Slf4j
public class AuthenticationController {
    
    private final PreferenceService preferenceService;
    
    /**
     * Mutation: updateUserPreferences
     * Actualiza las preferencias del usuario
     * 
     * Requiere autenticación
     * 
     * Ejemplo GraphQL:
     * mutation {
     *   updateUserPreferences(input: {
     *     userId: 1
     *     searchRadiusKm: 100.0
     *     emailNotifications: true
     *   }) {
     *     userPreferenceId
     *     searchRadius
     *     emailNotifications
     *   }
     * }
     */
    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    public PreferenceBasicDTO updateUserPreferences(@Argument UpdatePreferencesInput input) {
        log.info("✏️  GraphQL Mutation: updateUserPreferences(userId: {})", input.getUserId());
        return preferenceService.updateUserPreferences(input);
    }
    
    /**
     * Mutation: addFavoriteArtist
     * Agrega un artista favorito
     * 
     * Requiere autenticación
     * 
     * Ejemplo GraphQL:
     * mutation {
     *   addFavoriteArtist(userId: 1, spotifyId: "4q3ewBCX7sLwd24euuV69X") {
     *     spotifyId
     *     name
     *     genres
     *     popularity
     *     imageUrl
     *   }
     * }
     */
    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    public ArtistDTO addFavoriteArtist(
            @Argument Integer userId,
            @Argument String spotifyId) {
        
        log.info("✏️  GraphQL Mutation: addFavoriteArtist(userId: {}, spotifyId: {})", 
                userId, spotifyId);
        
        return preferenceService.addFavoriteArtist(userId, spotifyId);
    }
    
    /**
     * Mutation: removeFavoriteArtist
     * Elimina un artista favorito
     * 
     * Requiere autenticación
     * 
     * Ejemplo GraphQL:
     * mutation {
     *   removeFavoriteArtist(userId: 1, spotifyId: "4q3ewBCX7sLwd24euuV69X")
     * }
     */
    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    public String removeFavoriteArtist(
            @Argument Integer userId,
            @Argument String spotifyId) {
        
        log.info("✏️  GraphQL Mutation: removeFavoriteArtist(userId: {}, spotifyId: {})", 
                userId, spotifyId);
        
        return preferenceService.removeFavoriteArtist(userId, spotifyId);
    }
    
    /**
     * Mutation: addFavoriteGenre
     * Agrega un género favorito
     * 
     * Requiere autenticación
     * Al menos uno de genreId o genreName debe ser proporcionado
     * 
     * Ejemplo GraphQL:
     * mutation {
     *   addFavoriteGenre(userId: 1, genreId: 5) {
     *     musicGenreId
     *     name
     *     description
     *   }
     * }
     * 
     * O usando nombre:
     * mutation {
     *   addFavoriteGenre(userId: 1, genreName: "Rock") {
     *     musicGenreId
     *     name
     *   }
     * }
     */
    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    public MusicGenreDTO addFavoriteGenre(
            @Argument Integer userId,
            @Argument Integer genreId,
            @Argument String genreName) {
        
        log.info("✏️  GraphQL Mutation: addFavoriteGenre(userId: {}, genreId: {}, genreName: {})", 
                userId, genreId, genreName);
        
        return preferenceService.addFavoriteGenre(userId, genreId, genreName);
    }
    
    /**
     * Mutation: removeFavoriteGenre
     * Elimina un género favorito
     * 
     * Requiere autenticación
     * Al menos uno de genreId o genreName debe ser proporcionado
     * 
     * Ejemplo GraphQL:
     * mutation {
     *   removeFavoriteGenre(userId: 1, genreId: 5)
     * }
     * 
     * O usando nombre:
     * mutation {
     *   removeFavoriteGenre(userId: 1, genreName: "Rock")
     * }
     */
    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    public String removeFavoriteGenre(
            @Argument Integer userId,
            @Argument Integer genreId,
            @Argument String genreName) {
        
        log.info("✏️  GraphQL Mutation: removeFavoriteGenre(userId: {}, genreId: {}, genreName: {})", 
                userId, genreId, genreName);
        
        return preferenceService.removeFavoriteGenre(userId, genreId, genreName);
    }
}