package com.chetraseng.sunrise_task_flow_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponse {
  private Long id;
  private String content;
  private String author;
  private LocalDateTime createdAt;
  private String authorEmail;
}
