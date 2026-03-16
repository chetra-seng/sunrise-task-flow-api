package com.chetraseng.sunrise_task_flow_api.controllers;

import com.chetraseng.sunrise_task_flow_api.Services.TaskService;
import com.chetraseng.sunrise_task_flow_api.dto.*;
import com.chetraseng.sunrise_task_flow_api.model.TaskStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

// TODO: Add remaining imports as you implement each endpoint
// TODO: Inject your TaskService using constructor injection (@RequiredArgsConstructor)

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;

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

    @GetMapping
    public ResponseEntity<List<TaskResponse>>findAll(){
        return ResponseEntity.ok(taskService.findAll());
    }

    @GetMapping("/{id}")
    public  ResponseEntity<TaskResponse> findById(@PathVariable Long id){
        return ResponseEntity.ok(taskService.findById(id));
    }

    @PostMapping
    public ResponseEntity<TaskResponse> create(@RequestBody TaskRequest request){
        TaskResponse result = taskService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse>update(@PathVariable Long id, @RequestBody TaskRequest request){
        return ResponseEntity.ok(taskService.update(id, request));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        taskService.delete(id);
        return ResponseEntity.noContent().build();
    }


  // ═══════════════════════════════════════════════════════════════════════════
  // Exercise 3: Custom @Query Endpoint
  // ═══════════════════════════════════════════════════════════════════════════

  // TODO: GET /api/tasks/overdue → List<TaskResponse> (200)

    @GetMapping("/overdue")
    public ResponseEntity<List<TaskResponse>> findOverdueTasks(){
        return ResponseEntity.ok(taskService.findOverdueTasks());
    }

  // ═══════════════════════════════════════════════════════════════════════════
  // Exercise 4: Specifications + Pagination
  // ═══════════════════════════════════════════════════════════════════════════

  // TODO: GET /api/tasks/filter?status=&priority=&title=&projectId=&dueBefore=&labelId=&page=&size=
  //       → PaginationResponse<TaskResponse> (200)
  // Hint: Use FilterTaskDto and Pagination as method parameters —
  //       Spring binds query params to them automatically
    @GetMapping("/filter")
    public ResponseEntity<PaginationResponse<TaskResponse>> filterTasks(FilterTaskDto filter, Pagination pagination){
        return ResponseEntity.ok(taskService.filterTasks(filter, pagination));
    }

  // ═══════════════════════════════════════════════════════════════════════════
  // Exercise 5: Label Management on Tasks
  // ═══════════════════════════════════════════════════════════════════════════

  // TODO: PATCH /api/tasks/{id}/status?status= → TaskResponse (200 / 404)
  // Hint: Use @RequestParam TaskStatus status

    @PatchMapping("/{id}/status")
    public ResponseEntity<TaskResponse> updateTaskStatus(@PathVariable Long id, @RequestParam TaskStatus status){
        return ResponseEntity.ok(taskService.updateStatus(id, status));
    }
  // TODO: POST /api/tasks/{taskId}/labels/{labelId} → TaskResponse (200 / 404)

    @PostMapping("/{taskId}/labels/{labelId}")
    public ResponseEntity<TaskResponse> addLabelToTask(@PathVariable Long taskId, @PathVariable Long labelId){
        return ResponseEntity.ok(taskService.addLabel(taskId,labelId));
    }
  // TODO: DELETE /api/tasks/{taskId}/labels/{labelId} → TaskResponse (200 / 404)
    @DeleteMapping("/{taskId}/labels/{labelId}")
    public ResponseEntity<TaskResponse> removeLabelFromTask(@PathVariable Long taskId, @PathVariable Long labelId){
        return ResponseEntity.ok(taskService.removeLabel(taskId, labelId));
    }
}
