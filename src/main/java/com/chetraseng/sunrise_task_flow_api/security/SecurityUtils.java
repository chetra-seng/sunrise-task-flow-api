package com.chetraseng.sunrise_task_flow_api.security;

import com.chetraseng.sunrise_task_flow_api.model.Role;
import com.chetraseng.sunrise_task_flow_api.model.UserModel;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SecurityUtils {

    public Optional<UserModel> getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !(auth.getPrincipal() instanceof UserModel)) {
            return Optional.empty();
        }

        return Optional.of((UserModel) auth.getPrincipal());
    }

    public boolean isCurrentUserAdmin() {
        return getCurrentUser()
                .map(u -> u.getRole() == Role.ADMIN)
                .orElse(false);
    }
}