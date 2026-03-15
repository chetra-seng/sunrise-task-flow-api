package com.chetraseng.sunrise_task_flow_api.dto;

import com.chetraseng.sunrise_task_flow_api.model.Priority;
import com.chetraseng.sunrise_task_flow_api.model.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskRequest {
  private String title;
  private String description;

  // ═══════════════════════════════════════════════════════════════════════════
  // Exercise 1: Add the following fields
  // ═══════════════════════════════════════════════════════════════════════════

  // TODO: Add 'projectId' field — Long
  // TODO: Add 'priority' field — Priority (import from model package)
  // TODO: Add 'status' field — TaskStatus (import from model package)
  // TODO: Add 'dueDate' field — java.time.LocalDate

  private long projectId;
  private Priority priority;
  private TaskStatus status;
  private LocalDate dueDate;
}
