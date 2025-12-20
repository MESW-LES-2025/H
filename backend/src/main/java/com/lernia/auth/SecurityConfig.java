package com.lernia.auth;

import com.lernia.auth.oauth2.CustomOAuth2UserService;
import com.lernia.auth.oauth2.OAuth2AuthenticationSuccessHandler;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
public class SecurityConfig {

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        public SecurityContextRepository securityContextRepository() {
                return new HttpSessionSecurityContextRepository();
        }

        @Bean
        SecurityFilterChain securityFilterChain(
                        HttpSecurity http,
                        @Value("${app.cors.allowed-origins:http://localhost:4200}") String corsOrigins,
                        SecurityContextRepository securityContextRepository,
                        CustomOAuth2UserService customOAuth2UserService,
                        OAuth2AuthenticationSuccessHandler oauth2AuthenticationSuccessHandler) throws Exception {

                http
                                .csrf(AbstractHttpConfigurer::disable)
                                .cors(cors -> cors
                                                .configurationSource(request -> {
                                                        CorsConfiguration configuration = new CorsConfiguration();
                                                        List<String> origins = Arrays.stream(corsOrigins.split(","))
                                                                        .map(String::trim)
                                                                        .filter(s -> !s.isEmpty())
                                                                        .collect(Collectors.toList());
                                                        configuration.setAllowedOriginPatterns(origins);
                                                        configuration.setAllowedMethods(List.of("GET", "POST", "PUT",
                                                                        "PATCH", "DELETE", "OPTIONS"));
                                                        configuration.setAllowedHeaders(List.of("*"));
                                                        configuration.setAllowCredentials(true);
                                                        return configuration;
                                                }))

                                .csrf(AbstractHttpConfigurer::disable)
                                .logout(logout -> logout
                                                .logoutUrl("/logout")
                                                .invalidateHttpSession(true)
                                                .clearAuthentication(true)
                                                .deleteCookies("JSESSIONID")
                                                .logoutSuccessHandler((request, response, authentication) -> {
                                                        response.setStatus(HttpServletResponse.SC_OK);
                                                        response.setContentType("application/json");
                                                        response.getWriter().write(
                                                                        "{\"message\": \"Logged out successfully\"}");
                                                }))
                                .oauth2Login(oauth2 -> oauth2
                                                .userInfoEndpoint(userInfo -> userInfo
                                                                .userService(customOAuth2UserService))
                                                .successHandler(oauth2AuthenticationSuccessHandler))
                                .exceptionHandling(exceptions -> exceptions
                                                .authenticationEntryPoint((request, response, authException) -> {
                                                        // For API requests, return 401 instead of redirecting to OAuth
                                                        String requestUri = request.getRequestURI();
                                                        if (requestUri.startsWith("/api/")) {
                                                                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                                                                response.setContentType("application/json");
                                                                response.getWriter().write(
                                                                                "{\"error\": \"Unauthorized\", \"message\": \"Please log in first\"}");
                                                        } else {
                                                                // For non-API requests, allow default OAuth redirect
                                                                response.sendRedirect("/oauth2/authorization/google");
                                                        }
                                                }))
                                .securityContext(
                                                context -> context.securityContextRepository(securityContextRepository))
                                .authorizeHttpRequests(auth -> auth
                                                // Preflight CORS
                                                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                                                // Login / Register / Logout públicos
                                                .requestMatchers(HttpMethod.POST, "/login", "/register", "/logout")
                                                .permitAll()
                                                .requestMatchers(HttpMethod.GET, "/login").permitAll()
                                                .requestMatchers(HttpMethod.POST, "api/auth/password/forgot",
                                                                "api/auth/password/reset")
                                                .permitAll()

                                                // OAuth2 endpoints
                                                .requestMatchers("/oauth2/**", "/login/oauth2/code/**").permitAll()

                                                // Delete account (requires auth)
                                                .requestMatchers(HttpMethod.DELETE, "/api/profile/delete/**")
                                                .authenticated()

                                                // Profile updates (requires auth)
                                                .requestMatchers(HttpMethod.PUT, "/api/profile/**").authenticated()
                                                .requestMatchers(HttpMethod.PATCH, "/api/profile/**").authenticated()

                                                // Favoritos (GET/POST/DELETE)
                                                .requestMatchers("/api/favorites/**").permitAll()
                                                .requestMatchers("/api/favorites").permitAll()

                                                // Endpoint de sessão e logout para o frontend
                                                .requestMatchers("/api/auth/me", "/api/auth/logout").permitAll()
                                                .requestMatchers("/api/users/**").permitAll()

                                                // Preflight CORS
                                                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                                                // Endpoint de sessão e logout para o frontend
                                                .requestMatchers("/api/auth/me", "/api/auth/logout").permitAll()

                                                // Endpoints GET públicos
                                                .requestMatchers(HttpMethod.GET,
                                                                "/api/profile/**",
                                                                "/api/courses",
                                                                "/api/courses/**",
                                                                "/api/university/**",
                                                                "/api/university",
                                                                "/api/area-of-study",
                                                                "/api/reviews/**",
                                                                "/api/scholarship/**")
                                                .permitAll()

                                                // Subscription endpoints (require auth)
                                                .requestMatchers(HttpMethod.POST, "/api/subscriptions").authenticated()
                                                .requestMatchers(HttpMethod.DELETE, "/api/subscriptions")
                                                .authenticated()
                                                .requestMatchers(HttpMethod.GET, "/api/users/me/status").authenticated()

                                                // Recommendations endpoint (require auth, premium check in controller)
                                                .requestMatchers(HttpMethod.GET, "/api/recommendations").authenticated()

                                                // Admin endpoints require ADMIN role
                                                .requestMatchers("/api/admin/**").permitAll()
                                                // TODO: .requestMatchers("/api/admin/**").hasRole("ADMIN")

                                                // Swagger
                                                .requestMatchers(
                                                                "/v3/api-docs/**",
                                                                "/swagger-ui/**",
                                                                "/swagger-ui.html")
                                                .permitAll()

                                                .anyRequest().authenticated());
                return http.build();
        }
}
