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

  @CreationTimestamp private LocalDateTime createdAt;

  // Owning side
  @ManyToOne
  @JoinColumn(name = "project_id")
  private ProjectModel project;

  // ═══════════════════════════════════════════════════════════════════════════
  // Exercise 1: Add the following fields
  // ═══════════════════════════════════════════════════════════════════════════

  // TODO: Add 'status' field — TaskStatus enum, default TaskStatus.TODO
  //       Annotations: @Enumerated(EnumType.STRING), @Column(nullable = false)

  // TODO: Add 'priority' field — Priority enum, default Priority.MEDIUM
  //       Annotations: @Enumerated(EnumType.STRING), @Column(nullable = false)

  // TODO: Add 'dueDate' field — java.time.LocalDate
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private TaskStatus status = TaskStatus.TODO;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Priority priority;

  private LocalDate dueDate;

  // ═══════════════════════════════════════════════════════════════════════════
  // Exercise 5: Add ManyToMany relationship with LabelModel
  // ═══════════════════════════════════════════════════════════════════════════

  // TODO: Add 'labels' field — List<LabelModel>, initialize as new ArrayList<>()
  //       @ManyToMany
  //       @JoinTable(
  //           name = "task_labels",
  //           joinColumns = @JoinColumn(name = "task_id"),
  //           inverseJoinColumns = @JoinColumn(name = "label_id")
  //       )

  // ═══════════════════════════════════════════════════════════════════════════
  // Exercise 6: Add OneToMany relationship with CommentModel
  // ═══════════════════════════════════════════════════════════════════════════

  // TODO: Add 'comments' field — List<CommentModel>, initialize as new ArrayList<>()
  //       @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
}
