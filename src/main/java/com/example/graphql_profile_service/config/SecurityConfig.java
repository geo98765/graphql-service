package com.example.graphql_profile_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.example.graphql_profile_service.service.CustomUserDetailsService;

import lombok.RequiredArgsConstructor;

import static org.springframework.security.config.Customizer.withDefaults;

/**
 * Security Configuration for GraphQL Profile Service
 * Configuración de seguridad para el servicio de perfiles GraphQL
 * 
 * ACTUALIZADO: GraphQL ahora requiere autenticación HTTP Basic Auth
 * Las credenciales se validan contra user-service mediante REST
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    /**
     * Configura la cadena de filtros de seguridad
     * GraphQL requiere autenticación HTTP Basic
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Deshabilitar CSRF para APIs GraphQL
                .csrf(csrf -> csrf.disable())

                // Configurar CORS
                .cors(withDefaults())

                // Configurar autorización de requests
                .authorizeHttpRequests(auth -> auth
                        // ============= GRAPHQL ENDPOINT =============
                        // GraphQL REQUIERE autenticación (HTTP Basic Auth)
                        .requestMatchers("/graphql", "/graphql/**").authenticated()

                        // ============= GRAPHIQL (Público para desarrollo) =============
                        .requestMatchers("/graphiql", "/graphiql/**").permitAll()

                        // ============= ACTUATOR =============
                        .requestMatchers("/actuator/**").permitAll()

                        // ============= OTRAS RUTAS =============
                        .anyRequest().authenticated())

                // Habilitar HTTP Basic Authentication
                .httpBasic(withDefaults())

                // Configurar política de sesiones como STATELESS
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

    /**
     * Configura el proveedor de autenticación DAO
     * Usa CustomUserDetailsService para validar credenciales contra user-service
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Bean del AuthenticationManager
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /**
     * Codificador de contraseñas usando BCrypt
     * Debe coincidir con el encoder de user-service
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}