package com.chetraseng.sunrise_task_flow_api.model;

import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "users")
public class UserModel {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "email", nullable = false, unique = true)
  private String email;

  @Column(name = "first_name", columnDefinition = "varchar(150)")
  private String firstName;

  @Column(name = "last_name", columnDefinition = "varchar(150)")
  private String lastName;
  private String password;
}
