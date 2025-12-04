// package com.example.rockstadiumgraphql.service;

// import java.security.Key;
// import java.util.Date;
// import java.util.HashMap;
// import java.util.Map;
// import java.util.function.Function;

// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.security.core.userdetails.UserDetails;
// import org.springframework.stereotype.Service;

// import io.jsonwebtoken.Claims;
// import io.jsonwebtoken.Jwts;
// import io.jsonwebtoken.SignatureAlgorithm;
// import io.jsonwebtoken.io.Decoders;
// import io.jsonwebtoken.security.Keys;
// import lombok.extern.slf4j.Slf4j;

// /**
//  * Servicio para generar y validar tokens JWT
//  * Service for generating and validating JWT tokens
//  */
// @Service
// @Slf4j
// public class JwtService {
    
//     @Value("${security.jwt.secret-key}")
//     private String secretKey;
    
//     @Value("${security.jwt.expiration-time}")
//     private long jwtExpiration;
    
//     /**
//      * Extrae el username (email) del token
//      * Extract username (email) from token
//      */
//     public String extractUsername(String token) {
//         return extractClaim(token, Claims::getSubject);
//     }
    
//     /**
//      * Extrae un claim específico del token
//      * Extract a specific claim from token
//      */
//     public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
//         final Claims claims = extractAllClaims(token);
//         return claimsResolver.apply(claims);
//     }
    
//     /**
//      * Genera un token JWT para el usuario
//      * Generate JWT token for user
//      */
//     public String generateToken(UserDetails userDetails) {
//         return generateToken(new HashMap<>(), userDetails);
//     }
    
//     /**
//      * Genera un token JWT con claims adicionales
//      */
//     public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
//         return buildToken(extraClaims, userDetails, jwtExpiration);
//     }
    
//     /**
//      * Obtiene el tiempo de expiración del token
//      */
//     public long getExpirationTime() {
//         return jwtExpiration;
//     }
    
//     /**
//      * Construye el token JWT
//      */
//     private String buildToken(
//             Map<String, Object> extraClaims,
//             UserDetails userDetails,
//             long expiration) {
        
//         return Jwts.builder()
//                 .setClaims(extraClaims)
//                 .setSubject(userDetails.getUsername())
//                 .setIssuedAt(new Date(System.currentTimeMillis()))
//                 .setExpiration(new Date(System.currentTimeMillis() + expiration))
//                 .signWith(getSignInKey(), SignatureAlgorithm.HS256)
//                 .compact();
//     }
    
//     /**
//      * Valida si el token es válido
//      * Validate if token is valid
//      */
//     public boolean isTokenValid(String token, UserDetails userDetails) {
//         final String username = extractUsername(token);
//         return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
//     }
    
//     /**
//      * Verifica si el token ha expirado
//      * Check if token has expired
//      */
//     private boolean isTokenExpired(String token) {
//         return extractExpiration(token).before(new Date());
//     }
    
//     /**
//      * Extrae la fecha de expiración del token
//      * Extract expiration date from token
//      */
//     private Date extractExpiration(String token) {
//         return extractClaim(token, Claims::getExpiration);
//     }
    
//     /**
//      * Extrae todos los claims del token
//      * Extract all claims from token
//      */
//     private Claims extractAllClaims(String token) {
//         return Jwts.parserBuilder()
//                 .setSigningKey(getSignInKey())
//                 .build()
//                 .parseClaimsJws(token)
//                 .getBody();
//     }
    
//     /**
//      * Obtiene la clave de firma
//      * Get signing key
//      */
//     private Key getSignInKey() {
//         byte[] keyBytes = Decoders.BASE64.decode(secretKey);
//         return Keys.hmacShaKeyFor(keyBytes);
//     }
// }