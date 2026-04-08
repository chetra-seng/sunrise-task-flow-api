package com.chetraseng.sunrise_task_flow_api.controllers;

import com.chetraseng.sunrise_task_flow_api.dto.FilterTaskDto;
import com.chetraseng.sunrise_task_flow_api.dto.Pagination;
import com.chetraseng.sunrise_task_flow_api.dto.PaginationResponse;
import com.chetraseng.sunrise_task_flow_api.dto.TaskRequest;
import com.chetraseng.sunrise_task_flow_api.dto.TaskResponse;
import com.chetraseng.sunrise_task_flow_api.model.TaskStatus;
import com.chetraseng.sunrise_task_flow_api.services.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

  private final TaskService taskService;

  @GetMapping
  public ResponseEntity<List<TaskResponse>> findAll() {
    return ResponseEntity.ok(taskService.findAll());
  }

  @GetMapping("/{id}")
  public ResponseEntity<TaskResponse> findById(@PathVariable Long id) {
    return ResponseEntity.ok(taskService.findById(id));
  }

  @PostMapping
  public ResponseEntity<TaskResponse> create(@RequestBody TaskRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(taskService.create(request));
  }

  @PutMapping("/{id}")
  public ResponseEntity<TaskResponse> update(
      @PathVariable Long id, @RequestBody TaskRequest request) {
    return ResponseEntity.ok(taskService.update(id, request));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    taskService.delete(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/overdue")
  public ResponseEntity<List<TaskResponse>> findOverdueTasks() {
    return ResponseEntity.ok(taskService.findOverdueTasks());
  }

  @GetMapping("/filter")
  public ResponseEntity<PaginationResponse<TaskResponse>> filterTasks(
      FilterTaskDto filter, Pagination pagination) {
    return ResponseEntity.ok(taskService.filterTasks(filter, pagination));
  }

  @PatchMapping("/{id}/status")
  public ResponseEntity<TaskResponse> updateStatus(
      @PathVariable Long id, @RequestParam TaskStatus status) {
    return ResponseEntity.ok(taskService.updateStatus(id, status));
  }

  @PostMapping("/{taskId}/labels/{labelId}")
  public ResponseEntity<TaskResponse> addLabel(
      @PathVariable Long taskId, @PathVariable Long labelId) {
    return ResponseEntity.ok(taskService.addLabel(taskId, labelId));
  }

  @DeleteMapping("/{taskId}/labels/{labelId}")
  public ResponseEntity<TaskResponse> removeLabel(
      @PathVariable Long taskId, @PathVariable Long labelId) {
    return ResponseEntity.ok(taskService.removeLabel(taskId, labelId));
  }


}
