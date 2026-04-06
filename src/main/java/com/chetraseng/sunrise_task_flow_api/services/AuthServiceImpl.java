package com.chetraseng.sunrise_task_flow_api.services;

import com.chetraseng.sunrise_task_flow_api.config.JwtProperties;
import com.chetraseng.sunrise_task_flow_api.dto.AuthResponse;
import com.chetraseng.sunrise_task_flow_api.dto.LoginRequest;
import com.chetraseng.sunrise_task_flow_api.dto.RegisterRequest;
import com.chetraseng.sunrise_task_flow_api.model.Role;
import com.chetraseng.sunrise_task_flow_api.model.UserModel;
import com.chetraseng.sunrise_task_flow_api.repository.UserRepository;
import com.chetraseng.sunrise_task_flow_api.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final JwtProperties jwtProperties;

    @Override
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
        }
        UserModel user = new UserModel();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setRole(Role.USER);
        userRepository.save(user);
        return buildAuthResponse(user);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        UserModel user = userRepository.findByEmail(request.getEmail()).orElseThrow();
        return buildAuthResponse(user);
    }

    private AuthResponse buildAuthResponse(UserModel user) {
        return new AuthResponse(
                jwtService.generateToken(user),
                jwtService.generateRefreshToken(user),
                jwtProperties.getExpiration(),
                user.getEmail(),
                user.getRole().name()
        );
    }
}