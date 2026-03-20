package com.chetraseng.sunrise_task_flow_api.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
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
  private Boolean completed = false;

  private String assignee;

  @CreationTimestamp
  private LocalDateTime createdAt;

  // Owning side
  @ManyToOne
  @JoinColumn(name = "project_id")
  private ProjectModel project;
}
