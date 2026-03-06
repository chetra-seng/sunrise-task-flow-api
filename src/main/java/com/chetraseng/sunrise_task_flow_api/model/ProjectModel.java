package com.chetraseng.sunrise_task_flow_api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "projects")
public class ProjectModel {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String name;

  @Column(columnDefinition = "text")
  private String description;

  @CreationTimestamp
  private LocalDateTime createdAt;

  @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
  @JsonIgnore
  @Builder.Default
  List<TaskModel> tasks = new ArrayList<>();
}
