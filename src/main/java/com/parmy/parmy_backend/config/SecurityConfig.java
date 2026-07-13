package com.parmy.parmy_backend.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${cors.allowed-origins}")
    private String allowedOrigins;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * Configure Security Filter Chain
     * 
     * @param http HttpSecurity object
     * @return SecurityFilterChain
     * @throws Exception if configuration fails
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authz -> authz
                        // Allow all authentication endpoints
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/otp/**").permitAll()

                        // Allow OAuth2 endpoints
                        .requestMatchers("/login/oauth2/**").permitAll()
                        .requestMatchers("/oauth2/**").permitAll()

                        // Allow health check and error endpoints
                        .requestMatchers("/health").permitAll()
                        .requestMatchers("/error").permitAll()

                        // Allow contact form (no authentication required)
                        .requestMatchers("/api/contact/**").permitAll()

                        // Allow static file access
                        .requestMatchers("/purchases/**").permitAll()
                        .requestMatchers("/offer_letters/**").permitAll()
                        .requestMatchers("/gallery_uploads/**").permitAll()
                        // Project abstracts require authentication
                        .requestMatchers("/api/projects/*/abstract/**").authenticated()

                        // Public projects endpoints
                        .requestMatchers("/api/projects/**").permitAll()
                        // Public gallery endpoints
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/gallery").permitAll()

                        // Admin-only endpoints (require authentication and ADMIN role)
                        .requestMatchers("/api/invoices/**").hasRole("ADMIN")
                        .requestMatchers("/api/expenses/**").hasRole("ADMIN")
                        .requestMatchers("/api/offers/**").hasRole("ADMIN")
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        // User purchases endpoints (authenticated users)
                        .requestMatchers("/api/purchases/**").authenticated()

                        // Allow Swagger UI and API docs (if needed)
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/v3/api-docs/**").permitAll()

                        // All other requests need authentication
                        .anyRequest().authenticated())
                .oauth2Login(oauth2 -> oauth2
                        .defaultSuccessUrl("/api/auth/oauth2/success", true)
                        .failureUrl("/api/auth/oauth2/failure"))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Password Encoder Bean
     * 
     * @return BCryptPasswordEncoder instance
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * CORS Configuration
     * 
     * @return CorsConfigurationSource
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Allow specified origins
        configuration.setAllowedOrigins(Arrays.asList(allowedOrigins.split(",")));

        // Allow all HTTP methods
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // Allow all headers
        configuration.setAllowedHeaders(List.of("*"));

        // Allow credentials
        configuration.setAllowCredentials(true);

        // Cache preflight response for 1 hour
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}