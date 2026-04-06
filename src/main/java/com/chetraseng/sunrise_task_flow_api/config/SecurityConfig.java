package com.chetraseng.sunrise_task_flow_api.config;

import com.chetraseng.sunrise_task_flow_api.security.*;
import com.chetraseng.sunrise_task_flow_api.services.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final JwtAuthenticationEntryPoint entryPoint;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // Public — no authentication required
                        .requestMatchers("/api/auth/**").permitAll()

                        // ADMIN only — dashboard
                        .requestMatchers(HttpMethod.GET, "/api/dashboard/**").hasRole("ADMIN")

                        // ADMIN only — all mutating operations
                        .requestMatchers(HttpMethod.POST,   "/api/tasks/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT,    "/api/tasks/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH,  "/api/tasks/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/tasks/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.POST,   "/api/projects/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT,    "/api/projects/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/projects/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.POST,   "/api/labels/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT,    "/api/labels/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/labels/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.POST,   "/api/comments/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT,    "/api/comments/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/comments/**").hasRole("ADMIN")

                        // Authenticated users (USER or ADMIN) — all remaining GETs
                        .anyRequest().authenticated()
                )
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(e -> e.authenticationEntryPoint(entryPoint))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}