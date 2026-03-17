package com.chetraseng.sunrise_task_flow_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentResponse {
    private String author ;
    private String content  ;
    private Long id;
    private LocalDateTime createdAt ;



}
