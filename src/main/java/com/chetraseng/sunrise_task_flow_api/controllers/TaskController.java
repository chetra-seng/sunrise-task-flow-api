package com.chetraseng.sunrise_task_flow_api.controllers;

import com.chetraseng.sunrise_task_flow_api.dto.TaskRequest;
import com.chetraseng.sunrise_task_flow_api.dto.TaskResponse;
import com.chetraseng.sunrise_task_flow_api.services.TaskService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tasks")
public class TaskController {
  private final TaskService taskService;

  @GetMapping
  public List<TaskResponse> getAllTask() {
    return taskService.findAll();
  }

  @GetMapping("/{id}")
  public ResponseEntity<TaskResponse> getTaskById(@PathVariable Long id) {
    return taskService
        .findById(id)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @PostMapping
  public ResponseEntity<TaskResponse> createTask(@RequestBody TaskRequest request) {
    TaskResponse taskResponse = taskService.create(request.getTitle(), request.getDescription());
    return ResponseEntity.status(HttpStatus.CREATED).body(taskResponse);
  }

  @PutMapping("/{id}")
  public ResponseEntity<TaskResponse> updateTask(
          @PathVariable Long id,
          @RequestBody TaskRequest request) {
    Optional<TaskResponse> response = taskService.update(id, request.getTitle(), request.getDescription());

    return response.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
  }
}
