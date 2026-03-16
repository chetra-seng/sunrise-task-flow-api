package com.chetraseng.sunrise_task_flow_api.config;

import com.chetraseng.sunrise_task_flow_api.controllers.AuthController;
import com.chetraseng.sunrise_task_flow_api.controllers.DashboardController;
import com.chetraseng.sunrise_task_flow_api.controllers.RoleController;
import com.chetraseng.sunrise_task_flow_api.controllers.TaskController;
import com.chetraseng.sunrise_task_flow_api.dto.ErrorResponse;
import com.chetraseng.sunrise_task_flow_api.security.JwtFilter;
import com.chetraseng.sunrise_task_flow_api.security.LoggingFilter;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.Method;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.SecurityContextHolderFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {
  private final LoggingFilter loggingFilter;
  private final UserDetailsService userDetailsService;
  private final JwtFilter jwtFilter;
  private final ObjectMapper objectMapper;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(
            request ->
                request
                    .requestMatchers(AuthController.BASE_URL + "/**")
                    .permitAll()
                    .requestMatchers(HttpMethod.GET, TaskController.BASE_URL + "/**")
                    .hasRole("VIEWER")
                    .requestMatchers(HttpMethod.POST, TaskController.BASE_URL + "/**")
                    .hasRole("USER")
                    .requestMatchers(HttpMethod.PUT, TaskController.BASE_URL + "/**")
                    .hasRole("USER")
                    .requestMatchers(HttpMethod.PATCH, TaskController.BASE_URL + "/**")
                    .hasRole("USER")
                    .requestMatchers(HttpMethod.DELETE, TaskController.BASE_URL + "/**")
                    .hasRole("USER")
                    .requestMatchers(DashboardController.BASE_URL + "/**")
                    .hasRole("ADMIN")
                    .requestMatchers(RoleController.BASE_URL + "/users")
                    .hasRole("SUPER_ADMIN")
                    .anyRequest()
                    .authenticated())
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .addFilterBefore(loggingFilter, SecurityContextHolderFilter.class)
        .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
        .exceptionHandling(
            ex ->
                ex.authenticationEntryPoint(
                        (request, response, authException) -> {
                          response.setStatus(HttpStatus.UNAUTHORIZED.value());
                          response.setContentType(MediaType.APPLICATION_JSON_VALUE);

                          ErrorResponse error =
                              new ErrorResponse(
                                  HttpStatus.UNAUTHORIZED.value(),
                                  "unauthorized access",
                                  LocalDateTime.now());
                          objectMapper.writeValue(response.getOutputStream(), error);
                        })
                    .accessDeniedHandler(
                        (request, response, accessDeniedException) -> {
                          response.setStatus(HttpStatus.FORBIDDEN.value());
                          response.setContentType(MediaType.APPLICATION_JSON_VALUE);

                          ErrorResponse error =
                              new ErrorResponse(
                                  HttpStatus.FORBIDDEN.value(),
                                  "access denied",
                                  LocalDateTime.now());
                          objectMapper.writeValue(response.getOutputStream(), error);
                        }));

    return http.build();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowedOrigins(List.of("http://localhost:3000"));
    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
    config.setAllowedHeaders(List.of("*"));

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/api/**", config);
    return source;
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config) {
    return config.getAuthenticationManager();
  }

  @Bean
  public AuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
    provider.setPasswordEncoder(passwordEncoder());
    return provider;
  }
}
