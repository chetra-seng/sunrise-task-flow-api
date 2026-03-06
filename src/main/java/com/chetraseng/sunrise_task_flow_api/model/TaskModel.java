package com.chetraseng.sunrise_task_flow_api.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "tasks")
public class TaskModel {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "title", length = 100, nullable = false)
  private String title;

  @Column(columnDefinition = "text")
  private String description;

  @Builder.Default
  private Boolean completed = false;

  @Enumerated(EnumType.STRING)
  @Column(length = 20)
  @Builder.Default
  private Priority priority = Priority.MEDIUM;

  @Enumerated(EnumType.STRING)
  @Column(length = 20)
  @Builder.Default
  private TaskStatus status = TaskStatus.TODO;

  private LocalDate dueDate;

  @CreationTimestamp
  private LocalDateTime createdAt;

  @ManyToOne
  @JoinColumn(name = "project_id")
  private ProjectModel project;
}
