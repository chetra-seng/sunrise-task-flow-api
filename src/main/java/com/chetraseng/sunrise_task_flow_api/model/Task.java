package com.chetraseng.sunrise_task_flow_api.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class Task {
    private long id;
    private String title;
    private String description;
    private boolean completed;
    private LocalDateTime create_at;

    public Task(boolean completed) {
        this.completed = false;
    }

    public Task(LocalDateTime create_at) {
        this.create_at = LocalDateTime.now();
    }
}
