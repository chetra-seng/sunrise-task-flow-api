package com.chetraseng.sunrise_task_flow_api.security;

import com.chetraseng.sunrise_task_flow_api.model.UserModel;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Component
public class SecurityUtils {

    public Optional<UserModel> getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || !(auth.getPrincipal() instanceof UserModel)) {
            return Optional.empty();
        }
        return Optional.of((UserModel) auth.getPrincipal());
    }

    public UserModel requireCurrentUser() {
        return getCurrentUser()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated"));
    }

    public boolean isCurrentUserAdmin() {
        return getCurrentUser().map(user -> user.getRole() == com.chetraseng.sunrise_task_flow_api.model.Role.ADMIN).orElse(false);
    }
}