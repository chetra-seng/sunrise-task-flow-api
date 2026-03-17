package com.chetraseng.sunrise_task_flow_api.controllers;

import com.chetraseng.sunrise_task_flow_api.Services.TaskService;
import com.chetraseng.sunrise_task_flow_api.dto.*;
import com.chetraseng.sunrise_task_flow_api.model.TaskStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// TODO: Add remaining imports as you implement each endpoint
// TODO: Inject your TaskService using constructor injection (@RequiredArgsConstructor)

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

  // ═══════════════════════════════════════════════════════════════════════════
  // Exercise 1: Task CRUD
  // ═══════════════════════════════════════════════════════════════════════════
  // All methods must return ResponseEntity<T>

    private final TaskService taskService = null;
    @GetMapping
    public ResponseEntity<List<TaskResponse>> getAll() {

        return ResponseEntity.ok(taskService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getById(@PathVariable Long id) {

        return ResponseEntity.ok(taskService.findById(id));
    }
    @PostMapping
    public ResponseEntity<TaskResponse> create(@RequestBody TaskRequest request) {

        TaskResponse response = taskService.create(request);

        return ResponseEntity.status(201).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> update(
            @PathVariable Long id,
            @RequestBody TaskRequest request) {

        return ResponseEntity.ok(
                taskService.update(id, request)
        );
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {

        taskService.delete(id);

        return ResponseEntity.noContent().build();
    }

  // TODO: GET /api/tasks → List<TaskResponse> (200)

  // TODO: GET /api/tasks/{id} → TaskResponse (200 / 404)

  // TODO: POST /api/tasks → TaskResponse (201)
  // Hint: ResponseEntity.status(HttpStatus.CREATED).body(...)

  // TODO: PUT /api/tasks/{id} → TaskResponse (200 / 404)

  // TODO: DELETE /api/tasks/{id} → no body (204 / 404)
  // Hint: ResponseEntity.noContent().build()

  // ═══════════════════════════════════════════════════════════════════════════
  // Exercise 3: Custom @Query Endpoint
  // ═══════════════════════════════════════════════════════════════════════════

    @GetMapping("/overdue")
    public ResponseEntity<List<TaskResponse>> getOverdueTasks() {
        return ResponseEntity.ok(taskService.findOverdueTasks());
    }

  // TODO: GET /api/tasks/overdue → List<TaskResponse> (200)

  // ═══════════════════════════════════════════════════════════════════════════
  // Exercise 4: Specifications + Pagination
  // ═══════════════════════════════════════════════════════════════════════════

    @GetMapping("/filter")
    public ResponseEntity<PaginationResponse<TaskResponse>> filterTasks(
            FilterTaskDto filter, Pagination pagination) {

        var response = taskService.filterTasks(filter, pagination);

        return ResponseEntity.ok(response);
    }

  // TODO: GET /api/tasks/filter?status=&priority=&title=&projectId=&dueBefore=&labelId=&page=&size=
  //       → PaginationResponse<TaskResponse> (200)
  // Hint: Use FilterTaskDto and Pagination as method parameters —
  //       Spring binds query params to them automatically

  // ═══════════════════════════════════════════════════════════════════════════
  // Exercise 5: Label Management on Tasks
  // ═══════════════════════════════════════════════════════════════════════════

    @PatchMapping("/{id}/status")
    public ResponseEntity<TaskResponse> updateStatus(
            @PathVariable Long id,
            @RequestParam TaskStatus status) {
        return ResponseEntity.ok(taskService.updateStatus(id, status));
    }

    @PostMapping("/{taskId}/labels/{labelId}")
    public ResponseEntity<TaskResponse> addLabel(
            @PathVariable Long taskId,
            @PathVariable Long labelId) {
        return ResponseEntity.ok(taskService.addLabel(taskId, labelId));
    }

    @DeleteMapping("/{taskId}/labels/{labelId}")
    public ResponseEntity<TaskResponse> removeLabel(
            @PathVariable Long taskId,
            @PathVariable Long labelId) {
        return ResponseEntity.ok(taskService.removeLabel(taskId, labelId));
    }

  // TODO: PATCH /api/tasks/{id}/status?status= → TaskResponse (200 / 404)
  // Hint: Use @RequestParam TaskStatus status

  // TODO: POST /api/tasks/{taskId}/labels/{labelId} → TaskResponse (200 / 404)

  // TODO: DELETE /api/tasks/{taskId}/labels/{labelId} → TaskResponse (200 / 404)
}
