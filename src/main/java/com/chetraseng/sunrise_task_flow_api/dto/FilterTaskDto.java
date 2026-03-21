package com.chetraseng.sunrise_task_flow_api.dto;

import com.chetraseng.sunrise_task_flow_api.model.Priority;
import com.chetraseng.sunrise_task_flow_api.model.TaskStatus;
import lombok.Data;

import java.time.LocalDate;

@Data
public class FilterTaskDto {
  private Long projectId;
  private String title;
  private TaskStatus status;
  private Priority priority;
  private LocalDate dueBefore;
  private Long labelId;
}
