package com.chetraseng.sunrise_task_flow_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

public class AuthResponse {
    public AuthResponse(Object o, Object o1, int i, String email, String name) {
    }

    @Data
    @AllArgsConstructor
    public static class authResponse {
        private String token;
        private String refreshToken;
        private long expiresIn;
        private String email;
        private String role;
    }
}
