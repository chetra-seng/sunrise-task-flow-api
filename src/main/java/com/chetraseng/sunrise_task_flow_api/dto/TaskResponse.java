package com.chetraseng.sunrise_task_flow_api.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.chetraseng.sunrise_task_flow_api.model.Priority;
import com.chetraseng.sunrise_task_flow_api.model.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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

  // ═══════════════════════════════════════════════════════════════════════════
  // Exercise 1: Add the following fields
  // ═══════════════════════════════════════════════════════════════════════════

  // TODO: Add 'status' field — TaskStatus (import from model package)
  // TODO: Add 'priority' field — Priority (import from model package)
  // TODO: Add 'dueDate' field — java.time.LocalDate
  // TODO: Add 'labelNames' field — List<String>
  // TODO: Add 'commentCount' field — int

    private TaskStatus status;
    private Priority priority;
    private LocalDate dueDate;
    private List<String> labelNames;
    private int commentCount;
}
