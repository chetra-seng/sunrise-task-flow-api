package com.chetraseng.sunrise_task_flow_api.controllers;

import com.chetraseng.sunrise_task_flow_api.dto.TaskRequest;
import com.chetraseng.sunrise_task_flow_api.dto.TaskResponse;
import com.chetraseng.sunrise_task_flow_api.repository.TaskRepository;
import com.chetraseng.sunrise_task_flow_api.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// TODO: Add remaining imports as you implement each endpoint
// TODO: Inject your TaskService using constructor injection (@RequiredArgsConstructor)

@RestController
@RequestMapping("/api/tasks")
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
        return ResponseEntity.status(200/404).body(this.taskService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        taskService.delete(id);
        return ResponseEntity.noContent().build();
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
