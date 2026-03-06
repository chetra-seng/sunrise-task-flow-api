package com.chetraseng.sunrise_task_flow_api.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectResponse {
  private Long id;
  private String name;
  private String description;
  private LocalDateTime createdAt;
  private int taskCount;
}
