// package com.example.rockstadiumgraphql.service;

// import java.time.LocalDateTime;
// import java.util.HashSet;
// import java.util.Set;

// import org.springframework.security.authentication.AuthenticationManager;
// import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
// import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;

// import com.example.rockstadiumgraphql.dto.LoginUserDTO;
// import com.example.rockstadiumgraphql.dto.RegisterUserDTO;
// import com.example.rockstadiumgraphql.model.Profile;
// import com.example.rockstadiumgraphql.model.User;
// import com.example.rockstadiumgraphql.model.UserPreference;
// import com.example.rockstadiumgraphql.repository.ProfileRepository;
// import com.example.rockstadiumgraphql.repository.UserPreferenceRepository;
// import com.example.rockstadiumgraphql.repository.UserRepository;

// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;

// /**
//  * Servicio de autenticación para registro y login
//  * Authentication service for registration and login
//  */
// @Service
// @RequiredArgsConstructor
// @Slf4j
// public class AuthenticationService {
    
//     private final UserRepository userRepository;
//     private final ProfileRepository profileRepository;
//     private final UserPreferenceRepository userPreferenceRepository;
//     private final PasswordEncoder passwordEncoder;
//     private final AuthenticationManager authenticationManager;
    
//     /**
//      * Registrar nuevo usuario
//      * Register new user
//      */
//     @Transactional
//     public User signup(RegisterUserDTO input) {
//         log.info("📝 Registering new user: {}", input.getEmail());
        
//         // Validar que el email no exista | Validate email doesn't exist
//         if (userRepository.existsByEmail(input.getEmail())) {
//             throw new RuntimeException("Email already registered");
//         }
        
//         // Crear usuario | Create user
//         User user = User.builder()
//                 .email(input.getEmail())
//                 .password(passwordEncoder.encode(input.getPassword()))
//                 .enabled(true)
//                 .accountNonExpired(true)
//                 .accountNonLocked(true)
//                 .credentialsNonExpired(true)
//                 .createdAt(LocalDateTime.now())
//                 .updatedAt(LocalDateTime.now())
//                 .build();
        
//         // Asignar rol USER por defecto | Assign USER role by default
//         Set<String> roles = new HashSet<>();
//         roles.add("ROLE_USER");
//         user.setRoles(roles);
        
//         user = userRepository.save(user);
//         log.info("✅ User created with ID: {}", user.getUserId());
        
//         // Crear perfil | Create profile
//         Profile profile = Profile.builder()
//                 .name(input.getName())
//                 .user(user)
//                 .build();
//         profile = profileRepository.save(profile);
//         log.info("✅ Profile created with ID: {}", profile.getProfileId());
        
//         // Crear preferencias por defecto | Create default preferences
//         UserPreference preferences = UserPreference.builder()
//                 .profile(profile)
//                 .searchRadius(java.math.BigDecimal.valueOf(25.0))
//                 .emailNotifications(true)
//                 .build();
//         userPreferenceRepository.save(preferences);
//         log.info("✅ User preferences created");
        
//         return user;
//     }
    
//     /**
//      * Autenticar usuario
//      * Authenticate user
//      */
//     public User authenticate(LoginUserDTO input) {
//         log.info("🔐 Authenticating user: {}", input.getEmail());
        
//         // Autenticar con Spring Security | Authenticate with Spring Security
//         authenticationManager.authenticate(
//                 new UsernamePasswordAuthenticationToken(
//                         input.getEmail(),
//                         input.getPassword()
//                 )
//         );
        
//         // Buscar y retornar usuario | Find and return user
//         return userRepository.findByEmail(input.getEmail())
//                 .orElseThrow(() -> new RuntimeException("User not found"));
//     }
// }