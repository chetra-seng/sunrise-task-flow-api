package com.chetraseng.sunrise_task_flow_api.service;


import com.chetraseng.sunrise_task_flow_api.dto.*;
import com.chetraseng.sunrise_task_flow_api.model.TaskStatus;

import java.util.List;

public interface TaskService {
    List<TaskResponse> findAll();
    TaskResponse findById(Long id);
    TaskResponse create(TaskRequest request);
    TaskResponse update(Long id, TaskRequest request);
    void delete(Long id);
    List<TaskResponse> findOverdueTasks();
    PaginationResponse<TaskResponse> filterTasks(FilterTaskDto filter, Pagination pagination);
    TaskResponse updateStatus(Long id, TaskStatus status);
    TaskResponse addLabel(Long taskId, Long labelId);
    TaskResponse removeLabel(Long taskId, Long labelId);
}