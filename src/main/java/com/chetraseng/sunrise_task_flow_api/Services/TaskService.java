package com.chetraseng.sunrise_task_flow_api.Services;

import com.chetraseng.sunrise_task_flow_api.dto.*;

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
    PaginationResponse<TaskResponse> filterTasks(FilterTaskDto filter, Pagination pagination);
}
