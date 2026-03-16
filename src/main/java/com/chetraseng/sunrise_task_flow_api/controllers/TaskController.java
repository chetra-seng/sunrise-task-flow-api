package com.chetraseng.sunrise_task_flow_api.controllers;

import com.chetraseng.sunrise_task_flow_api.dto.FilterTaskDto;
import com.chetraseng.sunrise_task_flow_api.dto.Pagination;
import com.chetraseng.sunrise_task_flow_api.dto.PaginationResponse;
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

@AllArgsConstructor
@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    private final TaskService taskService;

    // ═══════════════════════════════════════════════════════════════════════════
    // Exercise 1: Task CRUD
    // ═══════════════════════════════════════════════════════════════════════════

    @GetMapping
    public ResponseEntity<List<TaskResponse>> getAllTasks() {
        return ResponseEntity.ok(taskService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.findById(id));
    }

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@RequestBody com.chetraseng.sunrise_task_flow_api.dto.TaskRequest request) {
        TaskResponse createdTask = taskService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> updateTask(@PathVariable Long id,
                                                   @RequestBody com.chetraseng.sunrise_task_flow_api.dto.TaskRequest request) {
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

    @GetMapping("/overdue")
    public ResponseEntity<List<TaskResponse>> getOverdueTasks() {
        return ResponseEntity.ok(taskService.findOverdueTasks());
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // Exercise 4: Specifications + Pagination
    // ═══════════════════════════════════════════════════════════════════════════

    @GetMapping("/filter")
    public ResponseEntity<PaginationResponse<TaskResponse>> filterTasks(
            FilterTaskDto filter,
            Pagination pagination) {
        return ResponseEntity.ok(taskService.filterTasks(filter, pagination));
    }

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
