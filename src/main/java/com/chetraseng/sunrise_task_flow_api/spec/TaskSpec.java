package com.chetraseng.sunrise_task_flow_api.spec;

import com.chetraseng.sunrise_task_flow_api.model.Priority;
import com.chetraseng.sunrise_task_flow_api.model.TaskModel;
import com.chetraseng.sunrise_task_flow_api.model.TaskStatus;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class TaskSpec {

  public static Specification<TaskModel> containsTitle(String title) {
    return (root, query, cb) ->
        cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%");
  }

  public static Specification<TaskModel> equalProjectId(Long projectId) {
    return (root, query, cb) -> cb.equal(root.get("project").get("id"), projectId);
  }

  public static Specification<TaskModel> hasStatus(TaskStatus status) {
    return (root, query, cb) -> cb.equal(root.get("status"), status);
  }

  public static Specification<TaskModel> hasPriority(Priority priority) {
    return (root, query, cb) -> cb.equal(root.get("priority"), priority);
  }

  public static Specification<TaskModel> dueBefore(LocalDate date) {
    return (root, query, cb) -> cb.lessThan(root.get("dueDate"), date);
  }

  public static Specification<TaskModel> hasLabel(Long labelId) {
    return (root, query, cb) -> {
      var labelJoin = root.join("labels");
      return cb.equal(labelJoin.get("id"), labelId);
    };
  }
}
