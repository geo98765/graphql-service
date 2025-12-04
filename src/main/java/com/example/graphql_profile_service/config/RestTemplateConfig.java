package com.example.graphql_profile_service.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * RestTemplate Configuration
 * Configura el cliente REST para comunicación entre microservicios
 * 
 * @LoadBalanced permite usar service discovery de Eureka
 * Esto significa que podemos usar nombres de servicio en lugar de URLs directas
 * Ejemplo: http://user-service/api/v1/users en lugar de http://localhost:8081/api/v1/users
 */
@Configuration
public class RestTemplateConfig {
    
    /**
     * Configura RestTemplate con balanceo de carga
     * El @LoadBalanced habilita el service discovery mediante Eureka
     * 
     * Esto permite:
     * - Usar nombres de servicio en URLs (http://user-service/...)
     * - Balanceo de carga automático si hay múltiples instancias
     * - Failover automático si una instancia falla
     */
    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}