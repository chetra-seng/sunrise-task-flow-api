package com.chetraseng.sunrise_task_flow_api.controllers;

import com.chetraseng.sunrise_task_flow_api.dto.request.LoginRequest;
import com.chetraseng.sunrise_task_flow_api.dto.request.RegisterRequest;
import com.chetraseng.sunrise_task_flow_api.dto.response.TokenResponse;
import com.chetraseng.sunrise_task_flow_api.services.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(AuthController.BASE_URL)
public class AuthController {
  public static final String BASE_URL = "/api/auth";

  private final AuthService authService;

  @PostMapping("/register")
  public ResponseEntity<TokenResponse> registerUser(@Valid @RequestBody RegisterRequest request) {
    return ResponseEntity.ok(authService.registerUser(request));
  }

  @PostMapping("/login")
  public ResponseEntity<TokenResponse> loginUser(@Valid @RequestBody LoginRequest request) {
    return ResponseEntity.ok(authService.loginUser(request));
  }
}
