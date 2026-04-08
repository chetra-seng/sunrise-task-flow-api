package com.chetraseng.sunrise_task_flow_api.dto;

import lombok.Data;
import org.jspecify.annotations.Nullable;

public class RegisterRequest {
    public String getEmail() {
        return "";
    }

    public @Nullable CharSequence getPassword() {
        return null;
    }

    @Data
    public static class registerRequest {
        private String email;
        private String password;
        private String firstName;
        private String lastName;
    }
}
