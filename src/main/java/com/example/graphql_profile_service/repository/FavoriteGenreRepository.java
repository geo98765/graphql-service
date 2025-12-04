package com.example.graphql_profile_service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.graphql_profile_service.model.*;
import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteGenreRepository extends JpaRepository<FavoriteGenre, Integer> {
    
    // Obtener todos los géneros favoritos de un perfil
    List<FavoriteGenre> findByProfileProfileId(Integer profileId);
    
    // Obtener géneros favoritos con paginación
    Page<FavoriteGenre> findByProfileProfileId(Integer profileId, Pageable pageable);
    
    // Verificar si un género ya es favorito
    boolean existsByProfileProfileIdAndMusicGenre_MusicGenreId(Integer profileId, Integer musicGenreId);
    
    // Buscar relación específica
    Optional<FavoriteGenre> findByProfileProfileIdAndMusicGenre_MusicGenreId(
            Integer profileId, Integer musicGenreId);
    
    // Contar géneros favoritos
    long countByProfileProfileId(Integer profileId);
    
    // Eliminar género favorito
    void deleteByProfileProfileIdAndMusicGenre_MusicGenreId(Integer profileId, Integer musicGenreId);
}