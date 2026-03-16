package com.chetraseng.sunrise_task_flow_api.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class UserRoleRequest {
    @NotNull(message = "user id must not be null")
    private Long userId;

    private List<Long> roleIds;
}
