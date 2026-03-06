package com.chetraseng.sunrise_task_flow_api.repository;

import com.chetraseng.sunrise_task_flow_api.dto.TaskSummary;
import com.chetraseng.sunrise_task_flow_api.model.Priority;
import com.chetraseng.sunrise_task_flow_api.model.TaskModel;
import com.chetraseng.sunrise_task_flow_api.model.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<TaskModel, Long>,
    JpaSpecificationExecutor<TaskModel> {

  // ── Derived Queries ────────────────────────────────────────────────────────

  List<TaskModel> findAllByCompleted(Boolean completed);

  List<TaskModel> findAllByStatus(TaskStatus status);

  List<TaskModel> findAllByPriority(Priority priority);

  List<TaskModel> findAllByPriorityAndStatus(Priority priority, TaskStatus status);

  List<TaskModel> findAllByTitleContainingIgnoreCase(String title);

  List<TaskModel> findAllByCompletedAndTitleContainingIgnoreCase(Boolean completed, String title);

  List<TaskModel> findAllByDueDateBefore(LocalDate date);

  long countByCompleted(Boolean completed);

  long countByStatus(TaskStatus status);

  boolean existsByTitle(String title);

  // ── Paginated Queries ──────────────────────────────────────────────────────

  Page<TaskModel> findAllByCompleted(Boolean completed, Pageable pageable);

  Page<TaskModel> findAllByStatus(TaskStatus status, Pageable pageable);

  // ── Custom JPQL Queries ────────────────────────────────────────────────────

  @Query("SELECT t FROM TaskModel t WHERE t.status = :status AND t.priority = :priority ORDER BY t.dueDate ASC")
  List<TaskModel> findByStatusAndPriorityOrdered(@Param("status") TaskStatus status,
                                                  @Param("priority") Priority priority);

  @Query("SELECT t FROM TaskModel t LEFT JOIN FETCH t.project WHERE t.id = :id")
  Optional<TaskModel> findByIdWithProject(@Param("id") Long id);

  @Query("SELECT t FROM TaskModel t JOIN t.project p WHERE p.name = :projectName")
  List<TaskModel> findByProjectName(@Param("projectName") String projectName);

  @Query("SELECT t FROM TaskModel t WHERE t.title LIKE %:keyword% AND t.completed = :completed")
  List<TaskModel> search(@Param("keyword") String keyword, @Param("completed") Boolean completed);

  // ── Native Query ───────────────────────────────────────────────────────────

  @Query(value = "SELECT id, title, completed FROM tasks WHERE project_id = :pid",
      nativeQuery = true)
  List<TaskSummary> findSummariesByProject(@Param("pid") Long projectId);

  // ── Modifying Queries ──────────────────────────────────────────────────────

  @Modifying
  @Query("UPDATE TaskModel t SET t.status = :status WHERE t.id = :id")
  int updateStatus(@Param("id") Long id, @Param("status") TaskStatus status);

  @Modifying
  @Query("DELETE FROM TaskModel t WHERE t.completed = true")
  int deleteAllCompleted();
}
