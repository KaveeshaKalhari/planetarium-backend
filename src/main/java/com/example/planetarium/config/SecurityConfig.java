package com.example.planetarium.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthFilter jwtAuthFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth

                // ── Fully public ──────────────────────────────────────────────
                .requestMatchers(
                    "/api/v1/signup",
                    "/api/v1/login",
                    "/api/v1/createuser",
                    "/api/v1/auth/google",
                    "/api/v1/auth/google/callback",
                    "/api/v1/password-reset/**",
                    "/api/v1/contact"
                ).permitAll()

                // Public read-only (show calendar, blog list, seat map)
                .requestMatchers(HttpMethod.GET, "/api/v1/shows").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/shows/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/blogs").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/blogs/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/seats/show/**").permitAll()

                .requestMatchers("/api/v1/shows/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/v1/blogs/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/v1/bookings/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/v1/analytics/**").hasRole("ADMIN")
                .requestMatchers("/api/v1/getusers").hasRole("ADMIN")

                // Chat: admin sub-paths locked to ADMIN, user paths require any auth
                .requestMatchers("/api/v1/chat/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/v1/chat/**").authenticated()

                // Everything else requires a valid JWT (any role)
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5173"));
        configuration.setAllowedMethods(
            Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
        );
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}