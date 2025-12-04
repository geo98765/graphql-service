package com.example.graphql_profile_service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.graphql_profile_service.model.*;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Integer> {
    
    // Buscar perfil por user ID
    Optional<Profile> findByUserId(Integer userId);
    
    // Verificar si existe perfil para un usuario
    boolean existsByUserId(Integer userId);
}