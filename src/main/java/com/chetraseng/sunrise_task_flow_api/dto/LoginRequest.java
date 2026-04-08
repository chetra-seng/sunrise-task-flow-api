package com.chetraseng.sunrise_task_flow_api.dto;

import lombok.Data;

public class LoginRequest {
    public String getEmail() {
        return "";
    }

    @Data
    public static class loginRequest {
        private String email;
        private String password;
    }
}
