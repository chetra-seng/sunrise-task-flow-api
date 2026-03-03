package com.chetraseng.sunrise_task_flow_api.dto;

import com.chetraseng.sunrise_task_flow_api.model.Task;
import lombok.Data;

@Data
public class TaskRequest {
    private String title;
    private String description;
}
