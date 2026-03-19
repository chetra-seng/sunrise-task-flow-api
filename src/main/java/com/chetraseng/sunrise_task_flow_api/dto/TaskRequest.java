package com.chetraseng.sunrise_task_flow_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name = "TaskRequest", description = "Task request object with title and description")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskRequest {
  @Schema(
      name = "title",
      description = "Task title max 100 character",
      example = "Implement JWT",
      requiredMode = Schema.RequiredMode.REQUIRED)
  private String title;

  @Schema(
      name = "description",
      description = "Task description",
      example = "User login and generate jwt",
      requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  private String description;
}
