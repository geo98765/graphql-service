package com.example.graphql_profile_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;

import graphql.scalars.ExtendedScalars;

/**
 * GraphQL Configuration
 * Configura tipos escalares personalizados para GraphQL
 */
@Configuration
public class GraphQLConfig {
    
    /**
     * Configura los tipos escalares extendidos de GraphQL
     * Registra tipos personalizados como BigDecimal, Long, DateTime, etc.
     * 
     * Estos escalares permiten trabajar con tipos de datos más complejos
     * en los schemas de GraphQL más allá de los tipos básicos (String, Int, Boolean, Float)
     */
    @Bean
    public RuntimeWiringConfigurer runtimeWiringConfigurer() {
        return wiringBuilder -> wiringBuilder
                // Tipo Long para IDs grandes
                .scalar(ExtendedScalars.GraphQLLong)
                
                // Tipo BigDecimal para números decimales precisos
                .scalar(ExtendedScalars.GraphQLBigDecimal)
                
                // Tipo DateTime para fechas y horas
                .scalar(ExtendedScalars.DateTime)
                
                // Tipo Date para fechas sin hora
                .scalar(ExtendedScalars.Date)
                
                // Tipo Time para horas sin fecha
                .scalar(ExtendedScalars.Time);
    }
}