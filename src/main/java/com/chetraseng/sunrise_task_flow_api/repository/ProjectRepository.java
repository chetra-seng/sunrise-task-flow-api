package com.chetraseng.sunrise_task_flow_api.repository;

import com.chetraseng.sunrise_task_flow_api.model.ProjectModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<ProjectModel, Long> {

  Optional<ProjectModel> findByName(String name);

  List<ProjectModel> findByNameContainingIgnoreCase(String keyword);

  boolean existsByName(String name);
}
