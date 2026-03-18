package com.chetraseng.sunrise_task_flow_api.controllers;

import com.chetraseng.sunrise_task_flow_api.dto.TaskRequest;
import com.chetraseng.sunrise_task_flow_api.dto.TaskResponse;
import com.chetraseng.sunrise_task_flow_api.model.TaskStatus;
import com.chetraseng.sunrise_task_flow_api.service.TaskService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// TODO: Add remaining imports as you implement each endpoint
// TODO: Inject your TaskService using constructor injection (@RequiredArgsConstructor)

@RestController
@RequestMapping("/api/tasks")
@AllArgsConstructor
public class TaskController {
    private final TaskService taskService;

    @GetMapping
    public ResponseEntity<List<TaskResponse>> getAllTasks() {
        return ResponseEntity.ok(taskService.findAll());
    }


    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.findById(id));
    }


    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@RequestBody TaskRequest request) {
        TaskResponse createdTask = taskService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
    }


    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> updateTask(@PathVariable Long id, @RequestBody TaskRequest request) {
        return ResponseEntity.ok(taskService.update(id, request));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.delete(id);
        return ResponseEntity.noContent().build();
    }

  // ═══════════════════════════════════════════════════════════════════════════
  // Exercise 3: Custom @Query Endpoint
  // ═══════════════════════════════════════════════════════════════════════════

  // TODO: GET /api/tasks/overdue → List<TaskResponse> (200)

  // ═══════════════════════════════════════════════════════════════════════════
  // Exercise 4: Specifications + Pagination
  // ═══════════════════════════════════════════════════════════════════════════

  // TODO: GET /api/tasks/filter?status=&priority=&title=&projectId=&dueBefore=&labelId=&page=&size=
  //       → PaginationResponse<TaskResponse> (200)
  // Hint: Use FilterTaskDto and Pagination as method parameters —
  //       Spring binds query params to them automatically

  // ═══════════════════════════════════════════════════════════════════════════
  // Exercise 5: Label Management on Tasks
  // ═══════════════════════════════════════════════════════════════════════════

  @PatchMapping("/{id}/status")
  public ResponseEntity<TaskResponse> updateTaskStatus(
          @PathVariable Long id,
          @RequestParam TaskStatus status) {
      return ResponseEntity.ok(taskService.updateStatus(id, status));
  }


    @PostMapping("/{taskId}/labels/{labelId}")
    public ResponseEntity<TaskResponse> addLabelToTask(
            @PathVariable Long taskId,
            @PathVariable Long labelId) {
        return ResponseEntity.ok(taskService.addLabel(taskId, labelId));
    }


    @DeleteMapping("/{taskId}/labels/{labelId}")
    public ResponseEntity<TaskResponse> removeLabelFromTask(
            @PathVariable Long taskId,
            @PathVariable Long labelId) {
        return ResponseEntity.ok(taskService.removeLabel(taskId, labelId));
    }
}
