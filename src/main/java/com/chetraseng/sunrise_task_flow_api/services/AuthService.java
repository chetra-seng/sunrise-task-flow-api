package com.chetraseng.sunrise_task_flow_api.services;

import com.chetraseng.sunrise_task_flow_api.dto.request.LoginRequest;
import com.chetraseng.sunrise_task_flow_api.dto.request.RegisterRequest;
import com.chetraseng.sunrise_task_flow_api.dto.response.TokenResponse;

public interface AuthService {
  TokenResponse registerUser(RegisterRequest request);
  TokenResponse loginUser(LoginRequest request);
}
