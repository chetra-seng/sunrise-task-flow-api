package com.chetraseng.sunrise_task_flow_api.dto;

import lombok.Data;

import java.time.LocalDateTime;
@Data
public class ProjectResponse {
    private Long id;
    private String name;
    private LocalDateTime createdAt;
    private int taskCount;
}
