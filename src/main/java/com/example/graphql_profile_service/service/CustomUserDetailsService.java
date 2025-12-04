package com.example.graphql_profile_service.service;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.graphql_profile_service.dto.UserProfileDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Custom UserDetailsService for GraphQL Service
 * Valida credenciales contra user-service mediante comunicación REST interna
 * 
 * IMPORTANTE: Este servicio NO almacena usuarios localmente,
 * delega toda la autenticación a user-service
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final UserServiceClient userServiceClient;

    /**
     * Carga un usuario por email para autenticación
     * Llamada REST a user-service para obtener credenciales
     * 
     * @param email Email del usuario (usado como username)
     * @return UserDetails con información del usuario
     * @throws UsernameNotFoundException si el usuario no existe
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.info("🔐 Loading user by email for authentication: {}", email);

        try {
            // Llamar a user-service para obtener información del usuario
            // Este endpoint interno incluye password y roles para autenticación
            UserProfileDTO userProfile = userServiceClient.getUserByEmail(email);

            log.info("✅ User loaded successfully: {}", email);

            // Convertir UserProfileDTO a UserDetails de Spring Security
            return User.builder()
                    .username(userProfile.getEmail())
                    .password(userProfile.getPassword()) // Password hasheado de user-service
                    .authorities(userProfile.getRoles() != null ? userProfile.getRoles().toArray(new String[0])
                            : new String[] {})
                    .accountExpired(userProfile.getAccountStatus() != null &&
                            userProfile.getAccountStatus().getAccountNonExpired() != null
                                    ? !userProfile.getAccountStatus().getAccountNonExpired()
                                    : false)
                    .accountLocked(userProfile.getAccountStatus() != null &&
                            userProfile.getAccountStatus().getAccountNonLocked() != null
                                    ? !userProfile.getAccountStatus().getAccountNonLocked()
                                    : false)
                    .credentialsExpired(userProfile.getAccountStatus() != null &&
                            userProfile.getAccountStatus().getCredentialsNonExpired() != null
                                    ? !userProfile.getAccountStatus().getCredentialsNonExpired()
                                    : false)
                    .disabled(userProfile.getAccountStatus() != null &&
                            userProfile.getAccountStatus().getEnabled() != null
                                    ? !userProfile.getAccountStatus().getEnabled()
                                    : false)
                    .build();

        } catch (Exception e) {
            log.error("❌ User not found or error loading user: {}", email, e);
            throw new UsernameNotFoundException("User not found with email: " + email, e);
        }
    }
}
