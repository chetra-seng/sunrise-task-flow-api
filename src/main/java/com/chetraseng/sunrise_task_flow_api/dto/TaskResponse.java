package com.chetraseng.sunrise_task_flow_api.dto;

import com.chetraseng.sunrise_task_flow_api.model.Priority;
import com.chetraseng.sunrise_task_flow_api.model.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskResponse {
  private Long id;
  private String title;
  private String description;
  private LocalDateTime createdAt;
  private String projectName;
  private Long projectId;
  private TaskStatus status;
  private Priority priority;
  private LocalDate dueDate;
  private List<String> labelNames;
  private int commentCount;
  private String ownerEmail;
}
