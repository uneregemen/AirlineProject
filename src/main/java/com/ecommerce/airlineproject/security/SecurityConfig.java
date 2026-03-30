package com.ecommerce.airlineproject.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable()) // REST API kullandığımız için CSRF kapatıyoruz
                // Oturumları (Session) kapattık, güvenlik sadece Token ile sağlanacak!
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // 1. Swagger, API Dokümantasyonu ve İç Hata sayfası (403 maskelerini önlemek için) herkese açık!
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html", "/error").permitAll()
                        // 2. Kayıt olma ve Giriş yapma API'leri herkese açık olmalı
                        .requestMatchers("/api/v1/auth/**").permitAll()

                        // --- HOCANIN PDF TABLOSUNDAKİ KURALLAR  ---

                        // Query Flight (Uçuş Arama/Listeleme) -> NO (Herkese açık)
                        .requestMatchers("/api/v1/flights/search", "/api/v1/flights/all").permitAll()
                        // Check-in -> NO (Herkese açık)
                        .requestMatchers("/api/v1/tickets/checkin", "/api/v1/tickets/{id}").permitAll()

                        // Add Flight (Uçuş Ekleme) -> YES (Giriş zorunlu)
                        .requestMatchers("/api/v1/flights/add", "/api/v1/flights/upload").authenticated()
                        // Buy Ticket (Bilet Alma) -> YES (Giriş zorunlu)
                        .requestMatchers("/api/v1/tickets/buy", "/api/v1/tickets/cancel/**").authenticated()
                        // Passenger List (Yolcu Listesi) -> YES (Giriş zorunlu)
                        .requestMatchers("/api/v1/tickets/passengers").authenticated()

                        // Geri kalan her şey için kimlik doğrulaması iste
                        .anyRequest().authenticated()
                )
                // Kendi yazdığımız güvenlik görevlisini (JwtFilter), Spring'in standart görevlisinden hemen ÖNCE kapıya koyuyoruz!
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();


    }

    @Bean
    public org.springframework.security.crypto.password.PasswordEncoder passwordEncoder() {
        return new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
    }

    @Bean
    public org.springframework.web.cors.CorsConfigurationSource corsConfigurationSource() {
        org.springframework.web.cors.CorsConfiguration configuration = new org.springframework.web.cors.CorsConfiguration();
        configuration.setAllowedOrigins(java.util.List.of("*"));
        configuration.setAllowedMethods(java.util.List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(java.util.List.of("*"));
        org.springframework.web.cors.UrlBasedCorsConfigurationSource source = new org.springframework.web.cors.UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}