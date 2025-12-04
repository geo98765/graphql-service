package com.example.graphql_profile_service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.graphql_profile_service.model.*;
import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteArtistRepository extends JpaRepository<FavoriteArtist, Integer> {
    
    // Obtener todos los artistas favoritos de un perfil
    List<FavoriteArtist> findByProfileProfileId(Integer profileId);
    
    // Obtener artistas favoritos con paginación
    Page<FavoriteArtist> findByProfileProfileId(Integer profileId, Pageable pageable);
    
    // Verificar si un artista ya es favorito
    boolean existsByProfileProfileIdAndSpotifyId(Integer profileId, String spotifyId);
    
    // Buscar relación específica
    Optional<FavoriteArtist> findByProfileProfileIdAndSpotifyId(Integer profileId, String spotifyId);
    
    // Contar artistas favoritos
    long countByProfileProfileId(Integer profileId);
    
    // Eliminar artista favorito
    void deleteByProfileProfileIdAndSpotifyId(Integer profileId, String spotifyId);
}