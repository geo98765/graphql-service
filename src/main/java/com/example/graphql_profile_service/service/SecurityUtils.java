package com.example.graphql_profile_service.service;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.example.graphql_profile_service.dto.UserProfileDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Utilidad de seguridad para validar permisos de acceso
 * Implementa el mismo patrón de validación que user-service
 * 
 * Reglas de Autorización:
 * - Usuarios con ROLE_ADMIN tienen acceso total sin restricciones
 * - Usuarios regulares (ROLE_USER) solo pueden acceder a su propia información
 * - Intentos de acceso no autorizado lanzan AccessDeniedException (403
 * Forbidden)
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SecurityUtils {

    private final UserServiceClient userServiceClient;

    /**
     * Valida que el usuario autenticado sea dueño del recurso o sea ADMIN
     * 
     * @param targetUserId ID del usuario propietario del recurso
     * @throws IllegalStateException si el usuario no está autenticado
     * @throws AccessDeniedException si el usuario no tiene permiso para acceder al
     *                               recurso
     */
    public void validateUserOwnership(Integer targetUserId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Verificar que haya autenticación
        if (authentication == null || !authentication.isAuthenticated()) {
            log.error("❌ No authenticated user found in SecurityContext");
            throw new IllegalStateException("User not authenticated");
        }

        String authenticatedEmail = authentication.getName();
        log.debug("🔍 Validating access for user: {} to resource owned by userId: {}",
                authenticatedEmail, targetUserId);

        // Verificar si el usuario autenticado es ADMIN
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        // Si es ADMIN, tiene acceso total sin restricciones
        if (isAdmin) {
            log.info("✅ ADMIN user {} has unrestricted access to userId: {}",
                    authenticatedEmail, targetUserId);
            return;
        }

        // Si no es ADMIN, obtener el usuario objetivo y verificar ownership
        UserProfileDTO targetUser;
        try {
            targetUser = userServiceClient.getUserProfile(targetUserId);
        } catch (Exception e) {
            log.error("❌ Error fetching target user {}: {}", targetUserId, e.getMessage());
            throw e;
        }

        // Verificar que el email del usuario autenticado coincida con el del recurso
        if (!targetUser.getEmail().equals(authenticatedEmail)) {
            log.warn("❌ Unauthorized access attempt: User {} trying to access resource owned by userId: {} (email: {})",
                    authenticatedEmail, targetUserId, targetUser.getEmail());
            throw new AccessDeniedException("You don't have permission to access this resource");
        }

        log.debug("✅ User validated: {} accessing own resource (userId: {})",
                authenticatedEmail, targetUserId);
    }

    /**
     * Obtiene el ID del usuario autenticado consultando user-service
     * 
     * @return userId del usuario autenticado
     * @throws IllegalStateException si el usuario no está autenticado
     */
    public Integer getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            log.error("❌ No authenticated user found in SecurityContext");
            throw new IllegalStateException("User not authenticated");
        }

        String email = authentication.getName();
        log.debug("🔍 Fetching userId for authenticated user: {}", email);

        UserProfileDTO user = userServiceClient.getUserByEmail(email);
        log.debug("✅ Authenticated user {} has userId: {}", email, user.getUserId());

        return user.getUserId();
    }

    /**
     * Verifica si el usuario autenticado tiene rol ADMIN
     * 
     * @return true si el usuario es ADMIN, false en caso contrario
     */
    public boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        boolean admin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        if (admin) {
            log.debug("👑 User {} has ADMIN role", authentication.getName());
        }

        return admin;
    }

    /**
     * Obtiene el email del usuario autenticado
     * 
     * @return email del usuario autenticado
     * @throws IllegalStateException si el usuario no está autenticado
     */
    public String getAuthenticatedUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            log.error("❌ No authenticated user found in SecurityContext");
            throw new IllegalStateException("User not authenticated");
        }

        return authentication.getName();
    }
}
