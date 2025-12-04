package com.example.graphql_profile_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * GraphQL Profile Service Application
 * Microservicio GraphQL para gestión de perfiles y preferencias de usuario
 * 
 * Puerto: 8084
 * 
 * Funcionalidades:
 * - Consultas GraphQL para perfiles y preferencias
 * - Gestión de artistas favoritos (integración con Spotify API)
 * - Gestión de géneros musicales favoritos
 * - Comunicación REST con user-service (8081)
 * - Registro en Eureka Server (8761)
 * - Seguridad con HTTP Basic Auth
 * 
 * Endpoints principales:
 * - /graphql - Endpoint GraphQL
 * - /graphiql - Interfaz GraphiQL para testing
 * - /actuator/health - Health check
 * 
 * Base de datos: Compartida con user-service (rockstadium_users)
 * Tablas gestionadas:
 * - user_preferences
 * - favorite_artists
 * - favorite_genres
 * - music_genres
 */
@SpringBootApplication
@EnableDiscoveryClient
public class GraphqlProfileServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(GraphqlProfileServiceApplication.class, args);
    }
}