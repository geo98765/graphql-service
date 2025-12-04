// package com.example.rockstadiumgraphql.controller;

// import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter;
// import org.springframework.graphql.execution.ErrorType;
// import org.springframework.security.access.AccessDeniedException;
// import org.springframework.security.authentication.BadCredentialsException;
// import org.springframework.stereotype.Component;

// import graphql.GraphQLError;
// import graphql.GraphqlErrorBuilder;
// import graphql.schema.DataFetchingEnvironment;
// import io.jsonwebtoken.ExpiredJwtException;
// import io.jsonwebtoken.security.SignatureException;
// import lombok.extern.slf4j.Slf4j;

// /**
//  * Manejador global de excepciones para GraphQL
//  * Global exception handler for GraphQL
//  */
// @Component
// @Slf4j
// public class GlobalExceptionHandler extends DataFetcherExceptionResolverAdapter {
    
//     @Override
//     protected GraphQLError resolveToSingleError(Throwable ex, DataFetchingEnvironment env) {
//         log.error("GraphQL Error: {}", ex.getMessage(), ex);
        
//         // Credenciales incorrectas | Bad credentials
//         if (ex instanceof BadCredentialsException) {
//             return GraphqlErrorBuilder.newError(env)
//                     .errorType(ErrorType.UNAUTHORIZED)
//                     .message("The email or password is incorrect")
//                     .build();
//         }
        
//         // Acceso denegado | Access denied
//         if (ex instanceof AccessDeniedException) {
//             return GraphqlErrorBuilder.newError(env)
//                     .errorType(ErrorType.FORBIDDEN)
//                     .message("You are not authorized to access this resource")
//                     .build();
//         }
        
//         // Token JWT inválido | Invalid JWT token
//         if (ex instanceof SignatureException) {
//             return GraphqlErrorBuilder.newError(env)
//                     .errorType(ErrorType.UNAUTHORIZED)
//                     .message("The JWT signature is invalid")
//                     .build();
//         }
        
//         // Token JWT expirado | Expired JWT token
//         if (ex instanceof ExpiredJwtException) {
//             return GraphqlErrorBuilder.newError(env)
//                     .errorType(ErrorType.UNAUTHORIZED)
//                     .message("The JWT token has expired")
//                     .build();
//         }
        
//         // Error genérico | Generic error
//         return GraphqlErrorBuilder.newError(env)
//                 .errorType(ErrorType.INTERNAL_ERROR)
//                 .message(ex.getMessage() != null ? ex.getMessage() : "An unexpected error occurred")
//                 .build();
//     }
// }