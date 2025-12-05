package com.example.graphql_profile_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestTemplate;

/**
 * RestTemplate Configuration
 * Configura dos clientes REST:
 * 1. Con LoadBalancer (Eureka) para desarrollo local
 * 2. Sin LoadBalancer para URLs directas en Render
 * 
 * SOLUCIÓN: En Render, USER_SERVICE_URL es una URL completa (https://...)
 * que NO debe pasar por el LoadBalancer de Eureka
 */
@Configuration
public class RestTemplateConfig {

    @Value("${user-service.url:http://user-service}")
    private String userServiceUrl;

    /**
     * RestTemplate SIN LoadBalancer (PRIMARIO)
     * Usado cuando USER_SERVICE_URL es una URL completa (https://...)
     * 
     * En Render: https://microservicio-user-ecp9.onrender.com
     * En localhost: Fallback al loadBalancedRestTemplate
     * 
     * @Primary = Este será el RestTemplate por defecto
     */
    @Bean
    @Primary
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

    /**
     * RestTemplate CON LoadBalancer (Eureka)
     * Usado solo en desarrollo local cuando USER_SERVICE_URL es un nombre de
     * servicio
     * 
     * Ejemplo: http://user-service (nombre de servicio en Eureka)
     * 
     * NO se usa en Render porque las URLs son completas (https://...)
     */
    @Bean
    @LoadBalanced
    public RestTemplate loadBalancedRestTemplate() {
        return new RestTemplate();
    }
}