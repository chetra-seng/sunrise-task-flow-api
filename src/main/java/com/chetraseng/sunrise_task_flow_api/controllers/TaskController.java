package com.chetraseng.sunrise_task_flow_api.controllers;

import com.chetraseng.sunrise_task_flow_api.dto.TaskRequest;
import com.chetraseng.sunrise_task_flow_api.dto.TaskResponse;
import com.chetraseng.sunrise_task_flow_api.model.Priority;
import com.chetraseng.sunrise_task_flow_api.model.TaskStatus;
import com.chetraseng.sunrise_task_flow_api.services.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tasks")
public class TaskController {
  private final TaskService taskService;

  @GetMapping
  public ResponseEntity<List<TaskResponse>> getAllTasks(
      @RequestParam(required = false) Boolean completed) {
    return ResponseEntity.ok(taskService.findAll(completed));
  }

  @GetMapping("/{id}")
  public ResponseEntity<TaskResponse> getTaskById(@PathVariable Long id) {
    return ResponseEntity.ok(taskService.findById(id));
  }

  @PostMapping
  public ResponseEntity<TaskResponse> createTask(@RequestBody TaskRequest request) {
    TaskResponse taskResponse = taskService.create(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(taskResponse);
  }

  @PutMapping("/{id}")
  public ResponseEntity<TaskResponse> updateTask(@PathVariable Long id,
                                                  @RequestBody TaskRequest request) {
    return ResponseEntity.ok(taskService.update(id, request));
  }

  @PatchMapping("/{id}/complete")
  public ResponseEntity<TaskResponse> completeTask(@PathVariable Long id) {
    return ResponseEntity.ok(taskService.complete(id));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
    taskService.delete(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/filter")
  public ResponseEntity<List<TaskResponse>> filterTasks(
      @RequestParam Boolean completed,
      @RequestParam String title) {
    return ResponseEntity.ok(taskService.filterTask(completed, title));
  }

  // ── Pagination ─────────────────────────────────────────────────────────────

  @GetMapping("/paged")
  public ResponseEntity<Page<TaskResponse>> getTasksPaged(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(defaultValue = "createdAt") String sortBy,
      @RequestParam(defaultValue = "desc") String direction) {
    Sort sort = direction.equalsIgnoreCase("asc")
        ? Sort.by(sortBy).ascending()
        : Sort.by(sortBy).descending();
    return ResponseEntity.ok(taskService.findAll(PageRequest.of(page, size, sort)));
  }

  // ── Specification Search ───────────────────────────────────────────────────

  @GetMapping("/search")
  public ResponseEntity<Page<TaskResponse>> searchTasks(
      @RequestParam(required = false) Boolean completed,
      @RequestParam(required = false) String keyword,
      @RequestParam(required = false) Long projectId,
      @RequestParam(required = false) TaskStatus status,
      @RequestParam(required = false) Priority priority,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    return ResponseEntity.ok(
        taskService.searchTasks(completed, keyword, projectId, status, priority,
            PageRequest.of(page, size)));
  }
}
