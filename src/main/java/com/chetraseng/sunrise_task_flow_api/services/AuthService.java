package com.chetraseng.sunrise_task_flow_api.services;

import com.chetraseng.sunrise_task_flow_api.dto.AuthResponse;
import com.chetraseng.sunrise_task_flow_api.dto.LoginRequest;
import com.chetraseng.sunrise_task_flow_api.dto.RegisterRequest;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}