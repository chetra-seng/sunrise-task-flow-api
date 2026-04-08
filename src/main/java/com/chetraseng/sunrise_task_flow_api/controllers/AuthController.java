package com.chetraseng.sunrise_task_flow_api.controllers;

import com.chetraseng.sunrise_task_flow_api.dto.AuthResponse;
import com.chetraseng.sunrise_task_flow_api.dto.LoginRequest;
import com.chetraseng.sunrise_task_flow_api.dto.RegisterRequest;
import com.chetraseng.sunrise_task_flow_api.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

public class AuthController {
    @RestController
    @RequestMapping("/api/auth")
    @RequiredArgsConstructor
    public static class authController {

        private final AuthService service;

        @PostMapping("/register")
        public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest req) {
            return ResponseEntity.ok(service.register(req));
        }

        @PostMapping("/login")
        public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest req) {
            return ResponseEntity.ok(service.login(req));
        }
    }
}
