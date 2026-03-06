package com.chetraseng.sunrise_task_flow_api.specification;

import com.chetraseng.sunrise_task_flow_api.model.Priority;
import com.chetraseng.sunrise_task_flow_api.model.TaskModel;
import com.chetraseng.sunrise_task_flow_api.model.TaskStatus;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class TaskSpecification {

  public static Specification<TaskModel> hasStatus(TaskStatus status) {
    return (root, query, cb) -> cb.equal(root.get("status"), status);
  }

  public static Specification<TaskModel> hasPriority(Priority priority) {
    return (root, query, cb) -> cb.equal(root.get("priority"), priority);
  }

  public static Specification<TaskModel> isCompleted(Boolean completed) {
    return (root, query, cb) -> cb.equal(root.get("completed"), completed);
  }

  public static Specification<TaskModel> titleContains(String keyword) {
    return (root, query, cb) ->
        cb.like(cb.lower(root.get("title")), "%" + keyword.toLowerCase() + "%");
  }

  public static Specification<TaskModel> inProject(Long projectId) {
    return (root, query, cb) -> cb.equal(root.get("project").get("id"), projectId);
  }

  public static Specification<TaskModel> dueBefore(LocalDate date) {
    return (root, query, cb) -> cb.lessThan(root.get("dueDate"), date);
  }
}
