package com.chetraseng.sunrise_task_flow_api.controllers;

import com.chetraseng.sunrise_task_flow_api.dto.*;
import com.chetraseng.sunrise_task_flow_api.model.TaskStatus;
import com.chetraseng.sunrise_task_flow_api.service.TaskService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// TODO: Add remaining imports as you implement each endpoint
// TODO: Inject your TaskService using constructor injection (@RequiredArgsConstructor)

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@Data

public class TaskController {
    @Autowired
        private TaskService taskService;

        @GetMapping
    public List<TaskResponse> findAll() {
        return this.taskService.findAll();
    }


    @GetMapping("/{id}")
    public TaskResponse findById(@PathVariable Long id) {
        return this.taskService.findById(id);
    }

    @PostMapping
    public ResponseEntity<TaskResponse> create(@RequestBody TaskRequest request) {
        return ResponseEntity.status(201).body(this.taskService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> update(@PathVariable Long id, @RequestBody TaskRequest request) {
        return ResponseEntity.status(200).body(this.taskService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        taskService.delete(id);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/over")
    public ResponseEntity<List<TaskResponse>> findAllByStatus() {
        return ResponseEntity.status(200).body(this.taskService.findOverdueTasks());
    }

    @GetMapping("/filter")
    public ResponseEntity<PaginationResponse<TaskResponse>> filterTasks(
            @ModelAttribute FilterTaskDto filter,
            @ModelAttribute Pagination pagination) {
        return ResponseEntity.ok(taskService.filterTasks(filter, pagination));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<TaskResponse> updateStatus(@PathVariable Long id, @RequestBody TaskStatus status) {
            return ResponseEntity.status(200).body(this.taskService.updateStatus(id, status));
    }

    @PostMapping("/{taskId}/labels/{labelId}")
    public ResponseEntity<TaskResponse> addLabel(@PathVariable Long taskId, @PathVariable Long labelId) {
        return ResponseEntity.status(200).body(this.taskService.addLabel(taskId, labelId));
    }

    // Remove label from task
    @DeleteMapping("/{taskId}/labels/{labelId}")
    public ResponseEntity<TaskResponse> removeLabel(@PathVariable Long taskId,@PathVariable Long labelId) {
        return ResponseEntity.status(200).body(this.taskService.removeLabel(taskId, labelId));
    }
  // ═══════════════════════════════════════════════════════════════════════════
  // Exercise 1: Task CRUD
  // ═══════════════════════════════════════════════════════════════════════════
  // All methods must return ResponseEntity<T>

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

  // TODO: PATCH /api/tasks/{id}/status?status= → TaskResponse (200 / 404)
  // Hint: Use @RequestParam TaskStatus status

  // TODO: POST /api/tasks/{taskId}/labels/{labelId} → TaskResponse (200 / 404)

  // TODO: DELETE /api/tasks/{taskId}/labels/{labelId} → TaskResponse (200 / 404)
}
