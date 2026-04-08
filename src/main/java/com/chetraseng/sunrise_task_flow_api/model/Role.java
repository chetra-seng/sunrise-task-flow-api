package com.chetraseng.sunrise_task_flow_api.model;

public enum Role {
    USER, ADMIN;

    public String getAuthority() {
        return "ROLE_" + this.name();
    }
}