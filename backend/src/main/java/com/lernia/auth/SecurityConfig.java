package com.lernia.auth;

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
import org.springframework.beans.factory.annotation.Value;
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
      @Value("${app.cors.allowed-origins}") String corsOrigins,
      SecurityContextRepository securityContextRepository
  ) throws Exception {
    http
      .cors(cors -> cors
        .configurationSource(request -> {
          CorsConfiguration configuration = new CorsConfiguration();
          List<String> origins = Arrays.stream(corsOrigins.split(","))
              .map(String::trim)
              .filter(s -> !s.isEmpty())
              .collect(Collectors.toList());
          configuration.setAllowedOrigins(origins);
          configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
          configuration.setAllowedHeaders(List.of("*"));
          configuration.setAllowCredentials(true);
          return configuration;
        })
      )
      .csrf(AbstractHttpConfigurer::disable)
      .securityContext(context -> context.securityContextRepository(securityContextRepository))
      .authorizeHttpRequests(auth -> auth
        .requestMatchers(HttpMethod.POST, "/register", "/login").permitAll()
        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() 
        .requestMatchers(HttpMethod.GET, 
            "/api/profile/**", 
            "/api/courses/**", 
            "/api/courses/search", 
            "/api/university/**", 
            "/api/area-of-study",
            "/api/reviews/**" 
        ).permitAll()
        .anyRequest().authenticated()
      );

    return http.build();
  }
}
