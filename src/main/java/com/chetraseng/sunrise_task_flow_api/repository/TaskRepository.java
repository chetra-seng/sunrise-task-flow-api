package com.chetraseng.sunrise_task_flow_api.repository;

import com.chetraseng.sunrise_task_flow_api.model.TaskModel;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class TaskRepository {
  private Map<Long, TaskModel> tasks = new ConcurrentHashMap<>();
  private AtomicLong counter = new AtomicLong(0);

  public List<TaskModel> findAll() {
    return tasks.values().stream().toList();
  }

  public TaskModel save(TaskModel task) {
    if (task.getId() == null) {
      long id = counter.incrementAndGet();
      task.setId(id);
    }

    tasks.put(task.getId(), task);

    return task;
  }

  public Boolean delete(Long id) {
    if (tasks.containsKey(id)) {
      tasks.remove(id);
      return true;
    }

    return false;
  }

  public Optional<TaskModel> findById(Long id) {
    return Optional.ofNullable(tasks.get(id));
  }
}
