package com.chetraseng.sunrise_task_flow_api.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

  @CreationTimestamp
  private LocalDateTime createdAt;

  @ManyToOne
  @JoinColumn(name = "project_id")
  private ProjectModel project;

  @ManyToOne
  @JoinColumn(name = "owner_id")
  private UserModel owner;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private TaskStatus status = TaskStatus.TODO;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Priority priority = Priority.MEDIUM;

  private LocalDate dueDate;

  @ManyToMany
  @JoinTable(
      name = "task_labels",
      joinColumns = @JoinColumn(name = "task_id"),
      inverseJoinColumns = @JoinColumn(name = "label_id"))
  private List<LabelModel> labels = new ArrayList<>();

  @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<CommentModel> comments = new ArrayList<>();
}
