package com.example.graphql_profile_service.service;

import java.util.List;

import com.example.graphql_profile_service.dto.*;

/**
 * Service interface for user preferences management
 * Interfaz del servicio para gestión de preferencias de usuario
 */
public interface PreferenceService {
    
    // ===== USER PROFILE =====
    
    /**
     * Get user profile from user-service
     * Obtiene el perfil del usuario desde user-service (llamada REST interna)
     */
    UserProfileDTO getUserProfile(Integer userId);
    
    // ===== PREFERENCES =====
    
    /**
     * Get user preferences with optional full lists
     * Obtiene las preferencias del usuario con listas opcionales completas
     */
    UserPreferencesDTO getUserPreferences(Integer userId, boolean includeFullLists);
    
    /**
     * Update user preferences
     * Actualiza las preferencias del usuario
     */
    PreferenceBasicDTO updateUserPreferences(UpdatePreferencesInput input);
    
    // ===== FAVORITE ARTISTS =====
    
    /**
     * Get paginated favorite artists
     * Obtiene artistas favoritos paginados
     */
    ArtistPageDTO getFavoriteArtists(Integer userId, int page, int size);
    
    /**
     * Add favorite artist
     * Agrega un artista favorito
     */
    ArtistDTO addFavoriteArtist(Integer userId, String spotifyId);
    
    /**
     * Remove favorite artist
     * Elimina un artista favorito
     */
    String removeFavoriteArtist(Integer userId, String spotifyId);
    
    // ===== FAVORITE GENRES =====
    
    /**
     * Get paginated favorite genres
     * Obtiene géneros favoritos paginados
     */
    GenrePageDTO getFavoriteGenres(Integer userId, int page, int size);
    
    /**
     * Add favorite genre
     * Agrega un género favorito
     */
    MusicGenreDTO addFavoriteGenre(Integer userId, Integer genreId, String genreName);
    
    /**
     * Remove favorite genre
     * Elimina un género favorito
     */
    String removeFavoriteGenre(Integer userId, Integer genreId, String genreName);
    
    /**
     * Get all available music genres
     * Obtiene todos los géneros musicales disponibles
     */
    List<MusicGenreDTO> getAllMusicGenres();
}