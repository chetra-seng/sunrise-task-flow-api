package com.chetraseng.sunrise_task_flow_api.repository;

import com.chetraseng.sunrise_task_flow_api.model.ProjectModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<ProjectModel, Long> {

  // ═══════════════════════════════════════════════════════════════════════════
  // Exercise 2: Derived Query Methods
  // ═══════════════════════════════════════════════════════════════════════════

  // TODO: Find a project by its name
  // Hint: Return type should be Optional<ProjectModel>

    Optional<ProjectModel> findByName(String name);

  // TODO: Check if a project with a given name already exists
  // Hint: Return type should be boolean

   boolean existsByName(String name);

  // ═══════════════════════════════════════════════════════════════════════════
  // Exercise 3: Projection Query (Dashboard)
  // ═══════════════════════════════════════════════════════════════════════════

  // TODO: getProjectStats() → List<ProjectStatsView>
  //   @Query — native SQL: for each project, return its name, task count, and done count
  //   Used by: GET /api/dashboard/summary
  //
  //   First, create the ProjectStatsView interface projection:
  //   public interface ProjectStatsView {
  //       String getProjectName();
  //       long getTaskCount();
  //       long getDoneCount();
  //   }
  //
  //   Then write the query (the column aliases must match the interface getter names):
  //   @Query(nativeQuery = true, value = """
  //       SELECT p.name          AS projectName,
  //              COUNT(t.id)     AS taskCount,
  //              SUM(CASE WHEN t.status = 'DONE' THEN 1 ELSE 0 END) AS doneCount
  //       FROM   projects p
  //       LEFT JOIN tasks t ON t.project_id = p.id
  //       GROUP BY p.id, p.name
  //       """)
}
