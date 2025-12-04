// package com.example.rockstadiumgraphql.config;

// import java.io.IOException;

// import org.springframework.beans.factory.annotation.Qualifier;
// import org.springframework.lang.NonNull;
// import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
// import org.springframework.security.core.Authentication;
// import org.springframework.security.core.context.SecurityContextHolder;
// import org.springframework.security.core.userdetails.UserDetails;
// import org.springframework.security.core.userdetails.UserDetailsService;
// import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
// import org.springframework.stereotype.Component;
// import org.springframework.web.filter.OncePerRequestFilter;
// import org.springframework.web.servlet.HandlerExceptionResolver;

// import com.example.rockstadiumgraphql.service.JwtService;

// import jakarta.servlet.FilterChain;
// import jakarta.servlet.ServletException;
// import jakarta.servlet.http.HttpServletRequest;
// import jakarta.servlet.http.HttpServletResponse;
// import lombok.extern.slf4j.Slf4j;

// /**
//  * Filtro para autenticación JWT en cada request
//  * Filter for JWT authentication on each request
//  */
// @Component
// @Slf4j
// public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
//     private final JwtService jwtService;
//     private final UserDetailsService userDetailsService;
//     private final HandlerExceptionResolver exceptionResolver;
    
//     /**
//      * Constructor - NO inyectamos SecurityConfig aquí para evitar ciclo
//      * Constructor - We DON'T inject SecurityConfig here to avoid cycle
//      */
//     public JwtAuthenticationFilter(
//             JwtService jwtService,
//             UserDetailsService userDetailsService,
//             @Qualifier("handlerExceptionResolver") HandlerExceptionResolver exceptionResolver) {
//         this.jwtService = jwtService;
//         this.userDetailsService = userDetailsService;
//         this.exceptionResolver = exceptionResolver;
//     }
    
//     @Override
//     protected void doFilterInternal(
//             @NonNull HttpServletRequest request,
//             @NonNull HttpServletResponse response,
//             @NonNull FilterChain filterChain) throws ServletException, IOException {
        
//         final String authHeader = request.getHeader("Authorization");
        
//         // Si no hay header o no empieza con Bearer, continuar sin autenticar
//         // If no header or doesn't start with Bearer, continue without authentication
//         if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//             filterChain.doFilter(request, response);
//             return;
//         }
        
//         try {
//             // Extraer el token JWT (quitar "Bearer " del inicio)
//             // Extract JWT token (remove "Bearer " prefix)
//             final String jwt = authHeader.substring(7);
//             final String userEmail = jwtService.extractUsername(jwt);
            
//             Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            
//             // Si tenemos email y no hay autenticación previa
//             // If we have email and no previous authentication
//             if (userEmail != null && authentication == null) {
//                 // Cargar detalles del usuario desde la BD
//                 // Load user details from database
//                 UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
                
//                 // Si el token es válido, autenticar al usuario
//                 // If token is valid, authenticate user
//                 if (jwtService.isTokenValid(jwt, userDetails)) {
//                     UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
//                             userDetails,
//                             null,
//                             userDetails.getAuthorities()
//                     );
                    
//                     authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//                     SecurityContextHolder.getContext().setAuthentication(authToken);
                    
//                     log.debug("✅ User authenticated: {} with roles: {}", 
//                             userEmail, userDetails.getAuthorities());
//                 }
//             }
            
//             filterChain.doFilter(request, response);
            
//         } catch (Exception exception) {
//             // Enviar excepción al manejador global
//             // Send exception to global handler
//             log.error("❌ JWT Authentication error: {}", exception.getMessage());
//             exceptionResolver.resolveException(request, response, null, exception);
//         }
//     }
// }