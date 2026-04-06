package com.chetraseng.sunrise_task_flow_api.security;

import com.chetraseng.sunrise_task_flow_api.dto.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
// REMOVE @RequiredArgsConstructor since we are not injecting anything anymore
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    // Create ObjectMapper manually and register the JavaTimeModule
    // This fixes the "LocalDateTime not supported" error in your tests
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ErrorResponse error = new ErrorResponse(
                401, "Authentication required", LocalDateTime.now());

        objectMapper.writeValue(response.getOutputStream(), error);
    }
}