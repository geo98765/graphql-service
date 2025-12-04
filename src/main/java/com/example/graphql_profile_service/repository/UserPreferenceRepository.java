package com.example.graphql_profile_service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.graphql_profile_service.model.*;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserPreferenceRepository extends JpaRepository<UserPreference, Integer> {
    
    // Buscar preferencias por profile ID
    Optional<UserPreference> findByProfileProfileId(Integer profileId);
    
    // Verificar si existen preferencias para un perfil
    boolean existsByProfileProfileId(Integer profileId);
}