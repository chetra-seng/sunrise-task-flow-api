package com.chetraseng.sunrise_task_flow_api.services;

import com.chetraseng.sunrise_task_flow_api.dto.request.LoginRequest;
import com.chetraseng.sunrise_task_flow_api.dto.request.RegisterRequest;
import com.chetraseng.sunrise_task_flow_api.dto.response.TokenResponse;
import com.chetraseng.sunrise_task_flow_api.exception.EmailExistException;
import com.chetraseng.sunrise_task_flow_api.exception.UnauthorizedException;

import com.chetraseng.sunrise_task_flow_api.model.UserModel;
import com.chetraseng.sunrise_task_flow_api.repository.UserRepository;
import com.chetraseng.sunrise_task_flow_api.security.JwtService;
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
  private final JwtService jwtService;

  @Override
  public TokenResponse registerUser(RegisterRequest request) {
    Boolean exist = userRepository.existsByEmail(request.getEmail());

    if (exist) {
      throw new EmailExistException("email already exist");
    }

    UserModel user = new UserModel();
    user.setEmail(request.getEmail());
    user.setFirstName(request.getFirstName());
    user.setLastName(request.getLastName());
    user.setPassword(passwordEncoder.encode(request.getPassword()));
    UserModel savedUser = userRepository.save(user);

    String token = jwtService.generateToken(null, savedUser);
    return new TokenResponse(token);
  }

  @Override
  public TokenResponse loginUser(LoginRequest request) {
    UserModel user = userRepository
        .findByEmail(request.getEmail())
        .orElseThrow(() -> new UnauthorizedException("user or password not match"));
    Authentication authentication =
        new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());
    authenticationManager.authenticate(authentication);

    String accessToken = jwtService.generateToken(null, user);

    return new TokenResponse(accessToken);
  }
}
