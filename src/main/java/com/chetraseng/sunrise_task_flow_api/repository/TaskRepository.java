package com.chetraseng.sunrise_task_flow_api.repository;

import com.chetraseng.sunrise_task_flow_api.model.Priority;
import com.chetraseng.sunrise_task_flow_api.model.TaskModel;
import com.chetraseng.sunrise_task_flow_api.model.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.scheduling.config.Task;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TaskRepository
    extends JpaRepository<TaskModel, Long>, JpaSpecificationExecutor<TaskModel> {

    List<TaskModel> findByProjectId(Long projectId);
    List<TaskModel> findByStatus(TaskStatus status);
    List<TaskModel>findByPriority(Priority priority);
    List<TaskModel>findByDueDate(LocalDateTime dueDate);
    long countByStatus(TaskStatus status);




    @Query("SELECT t FROM TaskModel t " +
            "WHERE t.dueDate < :today " +
            "AND t.status <> com.chetraseng.sunrise_task_flow_api.model.TaskStatus.DONE")
    List<TaskModel> findOverdueTasks(@Param("today") LocalDate today);




  // ═══════════════════════════════════════════════════════════════════════════
  // Exercise 1: Derived Query Methods
  // ═══════════════════════════════════════════════════════════════════════════

  // TODO: findByProjectId(Long projectId) → List<TaskModel>
  //   Used by: GET /api/projects/{id}/tasks

  // TODO: findByStatus(TaskStatus status) → List<TaskModel>

  // TODO: findByPriority(Priority priority) → List<TaskModel>

  // TODO: findByDueDateBefore(LocalDate date) → List<TaskModel>

  // TODO: countByStatus(TaskStatus status) → long
  //   Used by: GET /api/dashboard/summary

  // ═══════════════════════════════════════════════════════════════════════════
  // Exercise 3: Custom @Query Methods
  // ═══════════════════════════════════════════════════════════════════════════

  // TODO: findOverdueTasks(LocalDate today) → List<TaskModel>
  //   @Query — JPQL: dueDate < today AND status != DONE
  //   Used by: GET /api/tasks/overdue
  //   Hint:
  //   @Query("SELECT t FROM TaskModel t WHERE t.dueDate < :today AND t.status <> com.chetraseng.sunrise_task_flow_api.model.TaskStatus.DONE")
}
