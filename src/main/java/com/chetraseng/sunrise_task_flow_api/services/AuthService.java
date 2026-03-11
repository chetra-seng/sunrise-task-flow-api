package com.chetraseng.sunrise_task_flow_api.services;

import com.chetraseng.sunrise_task_flow_api.dto.request.LoginRequest;
import com.chetraseng.sunrise_task_flow_api.dto.request.RegisterRequest;

public interface AuthService {
  void registerUser(RegisterRequest request);
  void loginUser(LoginRequest request);
}
