package com.chetraseng.sunrise_task_flow_api.Services;

import com.chetraseng.sunrise_task_flow_api.dto.TaskRequest;
import com.chetraseng.sunrise_task_flow_api.dto.TaskResponse;

import java.util.List;

public interface TaskService {

    default List<TaskResponse> findAll() {
        return null;
    }
    TaskResponse findById(Long id);
    TaskResponse create(TaskRequest request);
    TaskResponse update(Long id, TaskRequest request);
    void delete(Long id);
    List<TaskResponse> findOverdueTasks();
}
