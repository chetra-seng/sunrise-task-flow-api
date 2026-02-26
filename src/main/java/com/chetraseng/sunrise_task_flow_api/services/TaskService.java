package com.chetraseng.sunrise_task_flow_api.services;

import com.chetraseng.sunrise_task_flow_api.dto.TaskResponse;

import java.util.List;
import java.util.Optional;

public interface TaskService {
    List<TaskResponse> findAll();
    Optional<TaskResponse> findById(Long id);
    TaskResponse create(String title, String description);
    Optional<TaskResponse> update(Long id, String title, String description);
    Optional<TaskResponse> complete(Long id);
    boolean delete(Long id);
}
