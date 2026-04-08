package com.chetraseng.sunrise_task_flow_api.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentModel {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(columnDefinition = "text", nullable = false)
  private String content;

  @Column(length = 50)
  private String author;

  @CreationTimestamp
  private LocalDateTime createdAt;

  @ManyToOne
  @JoinColumn(name = "task_id", nullable = false)
  private TaskModel task;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private UserModel user;

}
