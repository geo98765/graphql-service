package com.example.graphql_profile_service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.graphql_profile_service.model.*;
import java.util.List;
import java.util.Optional;

@Repository
public interface MusicGenreRepository extends JpaRepository<MusicGenre, Integer> {

    // Buscar género por nombre (case-insensitive)
    Optional<MusicGenre> findByNameIgnoreCase(String name);

    // Buscar géneros que contengan el texto especificado (para searchGenres)
    List<MusicGenre> findByNameContainingIgnoreCase(String name);

    // Verificar si existe género por nombre
    boolean existsByNameIgnoreCase(String name);
}