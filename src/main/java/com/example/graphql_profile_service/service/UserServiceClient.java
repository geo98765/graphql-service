package com.example.graphql_profile_service.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.example.graphql_profile_service.dto.UserProfileDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Client service for communication with user-service
 * Servicio cliente para comunicación REST con user-service
 * 
 * Este servicio actúa como un cliente REST que hace llamadas
 * internas al microservicio user-service para obtener información
 * de usuarios y perfiles.
 * 
 * COMUNICACIÓN ENTRE MICROSERVICIOS - REQUISITO CUMPLIDO
 * 
 * ACTUALIZADO: Ahora usa los endpoints internos de user-service
 * que no requieren autenticación para comunicación entre microservicios
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceClient {

    private final RestTemplate restTemplate;

    @Value("${user-service.url:http://user-service}")
    private String userServiceUrl;

    /**
     * Obtiene el perfil completo de un usuario desde user-service
     * Usa el endpoint INTERNO que no requiere autenticación
     * 
     * IMPORTANTE: Esta es la llamada REST interna entre microservicios
     * graphql-profile-service (8084) -> user-service -(8081)
     * 
     * @param userId ID del usuario
     * @return UserProfileDTO con la información del usuario
     * @throws RuntimeException si el usuario no existe o hay error en la
     *                          comunicación
     */
    public UserProfileDTO getUserProfile(Integer userId) {
        log.info("🔗 Llamada REST interna a user-service para obtener perfil del usuario: {}", userId);

        try {
            // Usar el endpoint INTERNO que no requiere autenticación
            String url = userServiceUrl + "/api/v1/users/internal/" + userId;

            log.debug("URL de comunicación interna (endpoint interno): {}", url);

            // Hacer la llamada REST GET al user-service
            ResponseEntity<UserProfileDTO> response = restTemplate.getForEntity(
                    url,
                    UserProfileDTO.class);

            // Validar respuesta
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                log.info("✅ Perfil de usuario obtenido exitosamente desde user-service");
                log.debug("   Email: {}, Profile: {}",
                        response.getBody().getEmail(),
                        response.getBody().getProfile() != null ? response.getBody().getProfile().getName() : "N/A");
                return response.getBody();
            }

            log.error("❌ Respuesta vacía desde user-service para userId: {}", userId);
            throw new RuntimeException("No se pudo obtener el perfil del usuario");

        } catch (HttpClientErrorException.NotFound e) {
            log.error("❌ Usuario no encontrado en user-service: {}", userId);
            throw new RuntimeException("User not found with id: " + userId);

        } catch (HttpClientErrorException e) {
            log.error("❌ Error HTTP al comunicarse con user-service: {} - {}",
                    e.getStatusCode(), e.getMessage());
            throw new RuntimeException("Error al comunicarse con user-service: " + e.getMessage());

        } catch (Exception e) {
            log.error("❌ Error inesperado al obtener perfil de usuario: {}", e.getMessage(), e);
            throw new RuntimeException("Error al obtener perfil de usuario: " + e.getMessage());
        }
    }

    /**
     * Verifica si un usuario existe en user-service
     * Usa el endpoint INTERNO /api/v1/users/{userId}/exists
     * 
     * @param userId ID del usuario
     * @return true si el usuario existe, false en caso contrario
     */
    public boolean userExists(Integer userId) {
        log.debug("🔍 Verificando existencia de usuario {} en user-service", userId);

        try {
            // Usar el endpoint INTERNO de verificación de existencia
            String url = userServiceUrl + "/api/v1/users/" + userId + "/exists";

            ResponseEntity<Boolean> response = restTemplate.getForEntity(
                    url,
                    Boolean.class);

            boolean exists = response.getStatusCode() == HttpStatus.OK &&
                    response.getBody() != null &&
                    response.getBody();

            log.debug("✅ Usuario {} existe: {}", userId, exists);
            return exists;

        } catch (HttpClientErrorException.NotFound e) {
            log.debug("Usuario {} no existe en user-service", userId);
            return false;

        } catch (Exception e) {
            log.warn("⚠️ Error al verificar existencia de usuario {}: {}",
                    userId, e.getMessage());
            return false;
        }
    }

    /**
     * Obtiene usuario por email
     * Usa el endpoint INTERNO de user-service
     * Usado tanto para autenticación como para validación de permisos
     * 
     * @param email Email del usuario
     * @return UserProfileDTO con información completa incluyendo password y roles
     * @throws RuntimeException si el usuario no existe
     */
    public UserProfileDTO getUserByEmail(String email) {
        log.info("🔐 Fetching user by email: {}", email);

        try {
            // Usar el endpoint INTERNO que incluye password y roles
            String url = userServiceUrl + "/api/v1/users/internal/email/" + email;

            log.debug("URL de comunicación interna: {}", url);

            ResponseEntity<UserProfileDTO> response = restTemplate.getForEntity(
                    url,
                    UserProfileDTO.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                log.info("✅ User fetched by email successfully from user-service");
                return response.getBody();
            }

            log.error("❌ Empty response from user-service for email: {}", email);
            throw new RuntimeException("User not found with email: " + email);

        } catch (HttpClientErrorException.NotFound e) {
            log.error("❌ User not found with email: {}", email);
            throw new RuntimeException("User not found with email: " + email);

        } catch (HttpClientErrorException e) {
            log.error("❌ HTTP error communicating with user-service: {} - {}",
                    e.getStatusCode(), e.getMessage());
            throw new RuntimeException("Error communicating with user-service: " + e.getMessage());

        } catch (Exception e) {
            log.error("❌ Unexpected error fetching user by email: {}", e.getMessage(), e);
            throw new RuntimeException("Error fetching user by email: " + e.getMessage());
        }
    }
}