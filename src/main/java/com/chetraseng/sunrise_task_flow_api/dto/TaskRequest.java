package com.chetraseng.sunrise_task_flow_api.dto;

import com.chetraseng.sunrise_task_flow_api.model.Priority;
import com.chetraseng.sunrise_task_flow_api.model.TaskStatus;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data @NoArgsConstructor @AllArgsConstructor
public class TaskRequest {

  @NotBlank(message = "Title is required")
  @Size(max = 100, message = "Title must be at most 100 characters")
  private String title;

  @Size(max = 1000, message = "Description must be at most 1000 characters")
  private String description;

  @Positive(message = "Project ID must be positive")
  private Long projectId;

  private Priority priority;
  private TaskStatus status;

  @FutureOrPresent(message = "Due date must be today or in the future")
  private LocalDate dueDate;
}