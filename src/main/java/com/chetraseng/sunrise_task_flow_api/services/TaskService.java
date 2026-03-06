package com.chetraseng.sunrise_task_flow_api.services;

import com.chetraseng.sunrise_task_flow_api.dto.TaskRequest;
import com.chetraseng.sunrise_task_flow_api.dto.TaskResponse;
import com.chetraseng.sunrise_task_flow_api.model.Priority;
import com.chetraseng.sunrise_task_flow_api.model.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TaskService {

  List<TaskResponse> findAll(Boolean completed);

  TaskResponse findById(Long id);

  TaskResponse create(TaskRequest request);

  TaskResponse update(Long id, TaskRequest request);

  TaskResponse complete(Long id);

  void delete(Long id);

  List<TaskResponse> filterTask(Boolean completed, String title);

  List<TaskResponse> findByStatus(TaskStatus status);

  List<TaskResponse> findByPriority(Priority priority);

  Page<TaskResponse> findAll(Pageable pageable);

  Page<TaskResponse> searchTasks(Boolean completed, String keyword, Long projectId,
                                  TaskStatus status, Priority priority, Pageable pageable);
}
