package com.chetraseng.sunrise_task_flow_api.controllers;

import com.chetraseng.sunrise_task_flow_api.dto.*;
import com.chetraseng.sunrise_task_flow_api.model.TaskModel;
import com.chetraseng.sunrise_task_flow_api.repository.TaskRepository;
import com.chetraseng.sunrise_task_flow_api.services.TaskService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(TaskController.BASE_URL)
public class TaskController {
  public static final String BASE_URL = "/api/tasks";

  private final TaskService taskService;

  @GetMapping
  public List<TaskResponse> getAllTask(@RequestParam(required = false) Boolean completed) {
    return taskService.findAll().stream()
        .filter(t -> completed == null || completed.equals(t.getCompleted()))
        .toList();
  }

  @GetMapping("/{id}")
  public TaskResponse getTaskById(@PathVariable Long id) {
    return taskService.findById(id);
  }

  @PostMapping
  public ResponseEntity<TaskResponse> createTask(@RequestBody TaskRequest request) {
    TaskResponse taskResponse = taskService.create(request.getTitle(), request.getDescription());
    return ResponseEntity.status(HttpStatus.CREATED).body(taskResponse);
  }

  @PutMapping("/{id}")
  public TaskResponse updateTask(@PathVariable Long id, @RequestBody TaskRequest request) {
    return taskService.update(id, request.getTitle(), request.getDescription());
  }

  @PatchMapping("/{id}/complete")
  public TaskResponse completeTask(@PathVariable Long id) {
    return taskService.complete(id);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteTask(@PathVariable Long id) {
    taskService.delete(id);
  }

  @GetMapping("/filter")
  public PaginationResponse<TaskResponse> filterTasks(FilterTaskDto filter, Pagination pagination) {
    return new PaginationResponse<>(taskService.filterTask(filter, pagination), pagination);
  }
}
