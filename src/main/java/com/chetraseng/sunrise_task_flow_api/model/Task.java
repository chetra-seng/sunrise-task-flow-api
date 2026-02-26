package com.chetraseng.sunrise_task_flow_api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Task {
    private Long id;
    private String title;
    private String description;
    private Boolean completed = false;
    private LocalDateTime createdAt = LocalDateTime.now();
}
