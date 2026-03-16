package com.chetraseng.sunrise_task_flow_api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "roles")
public class RoleModel {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "name", columnDefinition = "varchar(50)", unique = true, nullable = false)
  private String name;

  @OneToMany(mappedBy = "role", cascade = CascadeType.ALL)
  private List<UserRoleModel> users;
}
