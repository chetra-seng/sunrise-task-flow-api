package com.chetraseng.sunrise_task_flow_api.services;

import com.chetraseng.sunrise_task_flow_api.dto.request.LoginRequest;
import com.chetraseng.sunrise_task_flow_api.dto.request.RegisterRequest;
import com.chetraseng.sunrise_task_flow_api.exception.UnauthorizedException;

import com.chetraseng.sunrise_task_flow_api.model.UserModel;
import com.chetraseng.sunrise_task_flow_api.model.UserRole;
import com.chetraseng.sunrise_task_flow_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authenticationManager;

  @Override
  public void registerUser(RegisterRequest request) {
    UserModel user = new UserModel();
    user.setEmail(request.getEmail());
    user.setFirstName(request.getFirstName());
    user.setLastName(request.getLastName());
    user.setPassword(passwordEncoder.encode(request.getPassword()));
    user.setRole(UserRole.USER);
    userRepository.save(user);
  }

  @Override
  public void loginUser(LoginRequest request) {
    userRepository
        .findByEmail(request.getEmail())
        .orElseThrow(() -> new UnauthorizedException("user or password not match"));
    Authentication authentication =
        new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());
    authenticationManager.authenticate(authentication);
  }
}
