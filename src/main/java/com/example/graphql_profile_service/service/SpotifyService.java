package com.example.graphql_profile_service.service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.hc.core5.http.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.graphql_profile_service.dto.ArtistDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.ClientCredentials;
import se.michaelthelin.spotify.model_objects.specification.Artist;
import se.michaelthelin.spotify.model_objects.specification.Image;
import se.michaelthelin.spotify.requests.authorization.client_credentials.ClientCredentialsRequest;
import se.michaelthelin.spotify.requests.data.artists.GetArtistRequest;

/**
 * Service for Spotify API integration
 * Servicio para integración con Spotify Web API
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SpotifyService {
    
    @Value("${spotify.client.id}")
    private String clientId;
    
    @Value("${spotify.client.secret}")
    private String clientSecret;
    
    private SpotifyApi spotifyApi;
    
    /**
     * Obtiene un artista por su Spotify ID
     * Enriquece la información con datos de Spotify API
     * 
     * @param spotifyId Spotify ID del artista
     * @return ArtistDTO con información completa del artista
     */
    public ArtistDTO getArtistById(String spotifyId) {
        log.info("Obteniendo información del artista desde Spotify: {}", spotifyId);
        
        try {
            // Inicializar SpotifyApi si es necesario
            if (spotifyApi == null) {
                initializeSpotifyApi();
            }
            
            // Obtener access token
            String accessToken = getAccessToken();
            spotifyApi.setAccessToken(accessToken);
            
            // Crear request para obtener artista
            GetArtistRequest getArtistRequest = spotifyApi.getArtist(spotifyId).build();
            
            // Ejecutar request
            Artist artist = getArtistRequest.execute();
            
            // Mapear a DTO
            return mapToArtistDTO(artist);
            
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            log.error("Error obteniendo artista de Spotify: {}", e.getMessage(), e);
            
            // Retornar DTO básico en caso de error
            return ArtistDTO.builder()
                    .spotifyId(spotifyId)
                    .name("Unknown Artist")
                    .build();
        }
    }
    
    /**
     * Inicializa el cliente de Spotify API
     */
    private void initializeSpotifyApi() {
        log.info("Inicializando Spotify API client");
        spotifyApi = new SpotifyApi.Builder()
                .setClientId(clientId)
                .setClientSecret(clientSecret)
                .build();
    }
    
    /**
     * Obtiene un access token de Spotify usando Client Credentials Flow
     * 
     * @return Access token válido
     */
    private String getAccessToken() {
        try {
            ClientCredentialsRequest clientCredentialsRequest = spotifyApi.clientCredentials().build();
            ClientCredentials clientCredentials = clientCredentialsRequest.execute();
            
            log.debug("Access token obtenido exitosamente");
            return clientCredentials.getAccessToken();
            
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            log.error("Error obteniendo access token de Spotify: {}", e.getMessage(), e);
            throw new RuntimeException("Error al obtener access token de Spotify", e);
        }
    }
    
    /**
     * Mapea un objeto Artist de Spotify API a ArtistDTO
     * 
     * @param artist Artist object de Spotify API
     * @return ArtistDTO con la información mapeada
     */
    private ArtistDTO mapToArtistDTO(Artist artist) {
        // Obtener URL de imagen (primera imagen disponible)
        String imageUrl = null;
        if (artist.getImages() != null && artist.getImages().length > 0) {
            imageUrl = artist.getImages()[0].getUrl();
        }
        
        // Convertir géneros a lista
        List<String> genres = null;
        if (artist.getGenres() != null) {
            genres = Arrays.asList(artist.getGenres());
        }
        
        return ArtistDTO.builder()
                .spotifyId(artist.getId())
                .name(artist.getName())
                .genres(genres)
                .popularity(artist.getPopularity())
                .imageUrl(imageUrl)
                .externalUrl(artist.getExternalUrls() != null ? 
                        artist.getExternalUrls().get("spotify") : null)
                .followers(artist.getFollowers() != null ? 
                        artist.getFollowers().getTotal() : null)
                .build();
    }
}