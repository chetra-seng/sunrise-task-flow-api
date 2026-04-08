package com.chetraseng.sunrise_task_flow_api.services;

import com.chetraseng.sunrise_task_flow_api.dto.AuthResponse;
import com.chetraseng.sunrise_task_flow_api.dto.LoginRequest;
import com.chetraseng.sunrise_task_flow_api.dto.RegisterRequest;
import com.chetraseng.sunrise_task_flow_api.model.UserModel;
import com.chetraseng.sunrise_task_flow_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

public class AuthServiceImpl {
    @Service
    @RequiredArgsConstructor
    public static class authServiceImpl extends AuthService {

        private final UserRepository repo;
        private final PasswordEncoder encoder;
        private final JwtService jwtService;

        @Override
        public AuthResponse register(RegisterRequest req) {

            UserModel user = new UserModel();
            user.setEmail(req.getEmail());
            user.setPassword(encoder.encode(req.getPassword()));

            repo.save(user);

            return new AuthResponse(
                    jwtService.generateToken(user),
                    null,
                    0,
                    user.getEmail(),
                    user.getRole().name()
            );
        }

        @Override
        public AuthResponse login(LoginRequest req) {

            UserModel user = repo.findByEmail(req.getEmail())
                    .orElseThrow();

            return new AuthResponse(
                    jwtService.generateToken(user),
                    null,
                    0,
                    user.getEmail(),
                    user.getRole().name()
            );
        }
    }
}
