package com.chetraseng.sunrise_task_flow_api.services;

import com.chetraseng.sunrise_task_flow_api.dto.UserInfoDto;
import com.chetraseng.sunrise_task_flow_api.dto.request.RegisterRequest;

import java.util.List;

public interface UserService {
  void registerUser(RegisterRequest request);
}
