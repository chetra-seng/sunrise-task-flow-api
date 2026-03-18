package com.chetraseng.sunrise_task_flow_api.repository;

import com.chetraseng.sunrise_task_flow_api.model.Priority;
import com.chetraseng.sunrise_task_flow_api.model.TaskModel;
import com.chetraseng.sunrise_task_flow_api.model.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TaskRepository
    extends JpaRepository<TaskModel, Long>, JpaSpecificationExecutor<TaskModel> {

  // ═══════════════════════════════════════════════════════════════════════════
  // Exercise 1: Derived Query Methods
  // ═══════════════════════════════════════════════════════════════════════════

  List<TaskModel> findByProjectId(Long projectId);
  List<TaskModel> findByStatus(TaskStatus status);
  List<TaskModel> findByPriority(Priority priority);
  List<TaskModel> findByDueDateBefore(LocalDate date);
  List<TaskModel> countByStatus(TaskStatus status);

  // ═══════════════════════════════════════════════════════════════════════════
  // Exercise 3: Custom @Query Methods
  // ═══════════════════════════════════════════════════════════════════════════

  // TODO: findOverdueTasks(LocalDate today) → List<TaskModel>
  //   @Query — JPQL: dueDate < today AND status != DONE
  //   Used by: GET /api/tasks/overdue
  //   Hint:
  //   @Query("SELECT t FROM TaskModel t WHERE t.dueDate < :today AND t.status <> com.chetraseng.sunrise_task_flow_api.model.TaskStatus.DONE")
}
