package com.example.graphql_profile_service.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.graphql_profile_service.dto.*;
import com.example.graphql_profile_service.model.*;
import com.example.graphql_profile_service.repository.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of PreferenceService
 * Implementación del servicio de preferencias con control de acceso
 * 
 * Seguridad: Todos los métodos validan que el usuario autenticado
 * sea el propietario del recurso o tenga rol ADMIN
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PreferenceServiceImpl implements PreferenceService {
    
    // Repositorios
    private final ProfileRepository profileRepository;
    private final UserPreferenceRepository userPreferenceRepository;
    private final FavoriteArtistRepository favoriteArtistRepository;
    private final FavoriteGenreRepository favoriteGenreRepository;
    private final MusicGenreRepository musicGenreRepository;
    
    // Servicios externos
    private final UserServiceClient userServiceClient;
    private final SpotifyService spotifyService;
    
    // Seguridad
    private final SecurityUtils securityUtils;
    
    // Constantes
    private static final double DEFAULT_SEARCH_RADIUS = 50.0;
    private static final int MAX_FAVORITE_ARTISTS = 40;
    private static final int MAX_FAVORITE_GENRES = 30;
    
    // ===== USER PROFILE =====
    
    @Override
    public UserProfileDTO getUserProfile(Integer userId) {
        log.info("Obteniendo perfil de usuario: {}", userId);
        
        // VALIDAR PERMISOS: Solo el propietario o ADMIN pueden acceder
        securityUtils.validateUserOwnership(userId);
        
        // COMUNICACIÓN REST INTERNA - Llamada a user-service
        return userServiceClient.getUserProfile(userId);
    }
    
    // ===== PREFERENCES =====
    
    @Override
    @Transactional(readOnly = true)
    public UserPreferencesDTO getUserPreferences(Integer userId, boolean includeFullLists) {
        log.info("Obteniendo preferencias para usuario: {} (includeLists: {})", userId, includeFullLists);

        // VALIDAR PERMISOS: Solo el propietario o ADMIN pueden acceder
        securityUtils.validateUserOwnership(userId);

        // Obtener perfil del usuario
        Profile profile = getProfileByUserId(userId);

        // Obtener o crear preferencias
        UserPreference preference = userPreferenceRepository
                .findByProfileProfileId(profile.getProfileId())
                .orElseGet(() -> createDefaultPreference(profile));

        if (includeFullLists) {
            // Retornar con listas completas
            return buildFullPreferencesDTO(profile, preference);
        } else {
            // Retornar solo con conteos
            return buildSummaryPreferencesDTO(profile, preference);
        }
    }

    @Override
    @Transactional
    public PreferenceBasicDTO updateUserPreferences(UpdatePreferencesInput input) {
        log.info("Actualizando preferencias para usuario: {}", input.getUserId());

        // VALIDAR PERMISOS: Solo el propietario o ADMIN pueden modificar
        securityUtils.validateUserOwnership(input.getUserId());

        // Obtener perfil
        Profile profile = getProfileByUserId(input.getUserId());

        // Obtener o crear preferencias
        UserPreference preference = userPreferenceRepository
                .findByProfileProfileId(profile.getProfileId())
                .orElseGet(() -> createDefaultPreference(profile));

        // Actualizar campos si están presentes
        if (input.getSearchRadiusKm() != null) {
            preference.setSearchRadius(input.getSearchRadiusKm());
        }

        if (input.getEmailNotifications() != null) {
            preference.setEmailNotifications(input.getEmailNotifications());
        }

        // Guardar cambios
        preference = userPreferenceRepository.save(preference);

        log.info("✅ Preferencias actualizadas para usuario: {}", input.getUserId());

        // Retornar respuesta básica
        return PreferenceBasicDTO.builder()
                .userPreferenceId(preference.getUserPreferenceId())
                .profileId(profile.getProfileId())
                .searchRadius(preference.getSearchRadius())
                .emailNotifications(preference.getEmailNotifications())
                .build();
    }

    // ===== FAVORITE ARTISTS =====

    @Override
    @Transactional(readOnly = true)
    public ArtistPageDTO getFavoriteArtists(Integer userId, int page, int size) {
        log.info("Obteniendo artistas favoritos para usuario: {} (page: {}, size: {})",
                userId, page, size);

        // VALIDAR PERMISOS: Solo el propietario o ADMIN pueden acceder
        securityUtils.validateUserOwnership(userId);

        Profile profile = getProfileByUserId(userId);
        Pageable pageable = PageRequest.of(page, size);

        Page<FavoriteArtist> favoritesPage = favoriteArtistRepository
                .findByProfileProfileId(profile.getProfileId(), pageable);

        // Enriquecer con datos de Spotify
        List<ArtistDTO> artists = favoritesPage.getContent().stream()
                .map(fa -> spotifyService.getArtistById(fa.getSpotifyId()))
                .collect(Collectors.toList());

        return ArtistPageDTO.builder()
                .content(artists)
                .totalElements((int) favoritesPage.getTotalElements())
                .totalPages(favoritesPage.getTotalPages())
                .currentPage(favoritesPage.getNumber())
                .pageSize(favoritesPage.getSize())
                .hasNext(favoritesPage.hasNext())
                .hasPrevious(favoritesPage.hasPrevious())
                .build();
    }

    @Override
    @Transactional
    public ArtistDTO addFavoriteArtist(Integer userId, String spotifyId) {
        log.info("Agregando artista favorito: {} para usuario: {}", spotifyId, userId);

        // VALIDAR PERMISOS: Solo el propietario o ADMIN pueden modificar
        securityUtils.validateUserOwnership(userId);

        Profile profile = getProfileByUserId(userId);

        // Verificar si ya es favorito
        if (favoriteArtistRepository.existsByProfileProfileIdAndSpotifyId(
                profile.getProfileId(), spotifyId)) {
            log.warn("El artista ya es favorito: {}", spotifyId);
            throw new RuntimeException("Artist is already in favorites");
        }

        // Verificar límite
        long count = favoriteArtistRepository.countByProfileProfileId(profile.getProfileId());
        if (count >= MAX_FAVORITE_ARTISTS) {
            log.warn("Límite de artistas favoritos alcanzado: {}/{}", count, MAX_FAVORITE_ARTISTS);
            throw new RuntimeException("Maximum favorite artists limit reached: " + MAX_FAVORITE_ARTISTS);
        }

        // Obtener información del artista desde Spotify
        ArtistDTO artistInfo = spotifyService.getArtistById(spotifyId);

        // Guardar favorito
        FavoriteArtist favorite = FavoriteArtist.builder()
                .spotifyId(spotifyId)
                .profile(profile)
                .build();

        favoriteArtistRepository.save(favorite);

        log.info("✅ Artista agregado a favoritos: {}", artistInfo.getName());

        return artistInfo;
    }

    @Override
    @Transactional
    public String removeFavoriteArtist(Integer userId, String spotifyId) {
        log.info("Eliminando artista favorito: {} para usuario: {}", spotifyId, userId);

        // VALIDAR PERMISOS: Solo el propietario o ADMIN pueden modificar
        securityUtils.validateUserOwnership(userId);

        Profile profile = getProfileByUserId(userId);

        // Verificar si existe
        if (!favoriteArtistRepository.existsByProfileProfileIdAndSpotifyId(
                profile.getProfileId(), spotifyId)) {
            log.warn("El artista no está en favoritos: {}", spotifyId);
            throw new RuntimeException("Artist not found in favorites");
        }

        // Obtener nombre antes de eliminar
        ArtistDTO artistInfo = spotifyService.getArtistById(spotifyId);

        // Eliminar
        favoriteArtistRepository.deleteByProfileProfileIdAndSpotifyId(
                profile.getProfileId(), spotifyId);

        log.info("✅ Artista eliminado de favoritos: {}", artistInfo.getName());

        return "Artist '" + artistInfo.getName() + "' removed successfully";
    }

    // ===== FAVORITE GENRES =====

    @Override
    @Transactional(readOnly = true)
    public GenrePageDTO getFavoriteGenres(Integer userId, int page, int size) {
        log.info("Obteniendo géneros favoritos para usuario: {} (page: {}, size: {})",
                userId, page, size);

        // VALIDAR PERMISOS: Solo el propietario o ADMIN pueden acceder
        securityUtils.validateUserOwnership(userId);

        Profile profile = getProfileByUserId(userId);
        Pageable pageable = PageRequest.of(page, size);

        Page<FavoriteGenre> favoritesPage = favoriteGenreRepository
                .findByProfileProfileId(profile.getProfileId(), pageable);

        // Mapear a DTOs
        List<MusicGenreDTO> genres = favoritesPage.getContent().stream()
                .map(fg -> mapToMusicGenreDTO(fg.getMusicGenre()))
                .collect(Collectors.toList());

        return GenrePageDTO.builder()
                .content(genres)
                .totalElements((int) favoritesPage.getTotalElements())
                .totalPages(favoritesPage.getTotalPages())
                .currentPage(favoritesPage.getNumber())
                .pageSize(favoritesPage.getSize())
                .hasNext(favoritesPage.hasNext())
                .hasPrevious(favoritesPage.hasPrevious())
                .build();
    }

    @Override
    @Transactional
    public MusicGenreDTO addFavoriteGenre(Integer userId, Integer genreId, String genreName) {
        log.info("Agregando género favorito para usuario: {} (genreId: {}, genreName: {})",
                userId, genreId, genreName);

        // VALIDAR PERMISOS: Solo el propietario o ADMIN pueden modificar
        securityUtils.validateUserOwnership(userId);

        // Validar que al menos uno esté presente
        if (genreId == null && (genreName == null || genreName.trim().isEmpty())) {
            throw new RuntimeException("Must provide either genreId or genreName");
        }

        Profile profile = getProfileByUserId(userId);

        // Buscar género
        MusicGenre genre = findGenreByIdOrName(genreId, genreName);

        // Verificar si ya es favorito
        if (favoriteGenreRepository.existsByProfileProfileIdAndMusicGenre_MusicGenreId(
                profile.getProfileId(), genre.getMusicGenreId())) {
            log.warn("El género ya es favorito: {}", genre.getName());
            throw new RuntimeException("Genre is already in favorites");
        }

        // Verificar límite
        long count = favoriteGenreRepository.countByProfileProfileId(profile.getProfileId());
        if (count >= MAX_FAVORITE_GENRES) {
            log.warn("Límite de géneros favoritos alcanzado: {}/{}", count, MAX_FAVORITE_GENRES);
            throw new RuntimeException("Maximum favorite genres limit reached: " + MAX_FAVORITE_GENRES);
        }

        // Guardar favorito
        FavoriteGenre favorite = FavoriteGenre.builder()
                .musicGenre(genre)
                .profile(profile)
                .build();

        favoriteGenreRepository.save(favorite);

        log.info("✅ Género agregado a favoritos: {}", genre.getName());

        return mapToMusicGenreDTO(genre);
    }

    @Override
    @Transactional
    public String removeFavoriteGenre(Integer userId, Integer genreId, String genreName) {
        log.info("Eliminando género favorito para usuario: {} (genreId: {}, genreName: {})",
                userId, genreId, genreName);

        // VALIDAR PERMISOS: Solo el propietario o ADMIN pueden modificar
        securityUtils.validateUserOwnership(userId);

        // Validar que al menos uno esté presente
        if (genreId == null && (genreName == null || genreName.trim().isEmpty())) {
            throw new RuntimeException("Must provide either genreId or genreName");
        }

        Profile profile = getProfileByUserId(userId);

        // Buscar género
        MusicGenre genre = findGenreByIdOrName(genreId, genreName);

        // Verificar si existe
        if (!favoriteGenreRepository.existsByProfileProfileIdAndMusicGenre_MusicGenreId(
                profile.getProfileId(), genre.getMusicGenreId())) {
            log.warn("El género no está en favoritos: {}", genre.getName());
            throw new RuntimeException("Genre not found in favorites");
        }

        // Eliminar
        favoriteGenreRepository.deleteByProfileProfileIdAndMusicGenre_MusicGenreId(
                profile.getProfileId(), genre.getMusicGenreId());

        log.info("✅ Género eliminado de favoritos: {}", genre.getName());

        return "Genre '" + genre.getName() + "' removed successfully";
    }

    @Override
    @Transactional(readOnly = true)
    public List<MusicGenreDTO> getAllMusicGenres() {
        log.info("Obteniendo todos los géneros musicales");

        return musicGenreRepository.findAll().stream()
                .map(this::mapToMusicGenreDTO)
                .collect(Collectors.toList());
    }

    // ===== HELPER METHODS =====

    /**
     * Obtiene el perfil por userId
     * Lanza excepción si no existe
     */
    private Profile getProfileByUserId(Integer userId) {
        return profileRepository.findByUserId(userId)
                .orElseThrow(() -> {
                    log.error("Perfil no encontrado para userId: {}", userId);
                    return new RuntimeException("Profile not found for user: " + userId);
                });
    }

    /**
     * Crea preferencias por defecto para un perfil
     */
    private UserPreference createDefaultPreference(Profile profile) {
        log.info("Creando preferencias por defecto para profile: {}", profile.getProfileId());

        UserPreference preference = UserPreference.builder()
                .profile(profile)
                .searchRadius(DEFAULT_SEARCH_RADIUS)
                .emailNotifications(true)
                .build();

        return userPreferenceRepository.save(preference);
    }

    /**
     * Construye DTO completo de preferencias con listas
     */
    private UserPreferencesDTO buildFullPreferencesDTO(Profile profile, UserPreference preference) {
        List<FavoriteArtist> favoriteArtists = favoriteArtistRepository
                .findByProfileProfileId(profile.getProfileId());

        List<FavoriteGenre> favoriteGenres = favoriteGenreRepository
                .findByProfileProfileId(profile.getProfileId());

        // Enriquecer artistas con Spotify
        List<ArtistDTO> artists = favoriteArtists.stream()
                .map(fa -> spotifyService.getArtistById(fa.getSpotifyId()))
                .collect(Collectors.toList());

        // Mapear géneros
        List<MusicGenreDTO> genres = favoriteGenres.stream()
                .map(fg -> mapToMusicGenreDTO(fg.getMusicGenre()))
                .collect(Collectors.toList());

        return UserPreferencesDTO.builder()
                .userPreferenceId(preference.getUserPreferenceId())
                .profileId(profile.getProfileId())
                .searchRadius(preference.getSearchRadius())
                .emailNotifications(preference.getEmailNotifications())
                .favoriteArtists(artists)
                .favoriteGenres(genres)
                .favoriteArtistsCount(artists.size())
                .favoriteGenresCount(genres.size())
                .maxFavoriteArtists(MAX_FAVORITE_ARTISTS)
                .maxFavoriteGenres(MAX_FAVORITE_GENRES)
                .build();
    }

    /**
     * Construye DTO resumen de preferencias (solo conteos)
     */
    private UserPreferencesDTO buildSummaryPreferencesDTO(Profile profile, UserPreference preference) {
        long artistsCount = favoriteArtistRepository.countByProfileProfileId(profile.getProfileId());
        long genresCount = favoriteGenreRepository.countByProfileProfileId(profile.getProfileId());

        return UserPreferencesDTO.builder()
                .userPreferenceId(preference.getUserPreferenceId())
                .profileId(profile.getProfileId())
                .searchRadius(preference.getSearchRadius())
                .emailNotifications(preference.getEmailNotifications())
                .favoriteArtistsCount((int) artistsCount)
                .favoriteGenresCount((int) genresCount)
                .maxFavoriteArtists(MAX_FAVORITE_ARTISTS)
                .maxFavoriteGenres(MAX_FAVORITE_GENRES)
                .build();
    }

    /**
     * Busca un género por ID o nombre
     */
    private MusicGenre findGenreByIdOrName(Integer genreId, String genreName) {
        if (genreId != null) {
            return musicGenreRepository.findById(genreId)
                    .orElseThrow(() -> new RuntimeException("Genre not found with id: " + genreId));
        }

        return musicGenreRepository.findByNameIgnoreCase(genreName)
                .orElseThrow(() -> new RuntimeException("Genre not found with name: " + genreName));
    }

    /**
     * Mapea MusicGenre a MusicGenreDTO
     */
    private MusicGenreDTO mapToMusicGenreDTO(MusicGenre genre) {
        if (genre == null) {
            return null;
        }

        return MusicGenreDTO.builder()
                .musicGenreId(genre.getMusicGenreId())
                .name(genre.getName())
                .description(genre.getDescription())
                .build();
    }
}
