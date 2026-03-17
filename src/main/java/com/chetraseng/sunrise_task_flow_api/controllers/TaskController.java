package com.chetraseng.sunrise_task_flow_api.controllers;

import com.chetraseng.sunrise_task_flow_api.dto.*;
import com.chetraseng.sunrise_task_flow_api.model.TaskModel;
import com.chetraseng.sunrise_task_flow_api.repository.TaskRepository;
import com.chetraseng.sunrise_task_flow_api.services.TaskService;
import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(TaskController.BASE_URL)
@Tag(name = "Tasks", description = "Endpoints for managing tasks")
public class TaskController {
  public static final String BASE_URL = "/api/tasks";

  private final TaskService taskService;

  @GetMapping
  public List<TaskResponse> getAllTask(@RequestParam(required = false) Boolean completed) {
    return taskService.findAll().stream()
        .filter(t -> completed == null || completed.equals(t.getCompleted()))
        .toList();
  }

  @Operation(
      summary = "Get by ID",
      description = "Get full task info incluing project info based on ID")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Task found",
        content = @Content(schema = @Schema(implementation = TaskResponse.class))),
    @ApiResponse(
        responseCode = "404",
        description = "Task not found",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  @GetMapping("/{id}")
  public TaskResponse getTaskById(
      @Parameter(name = "id", description = "Task ID", example = "1") @PathVariable Long id) {
    return taskService.findById(id);
  }

  @PostMapping
  public ResponseEntity<TaskResponse> createTask(@RequestBody TaskRequest request) {
    TaskResponse taskResponse = taskService.create(request.getTitle(), request.getDescription());
    return ResponseEntity.status(HttpStatus.CREATED).body(taskResponse);
  }

  @PutMapping("/{id}")
  public TaskResponse updateTask(
      @Parameter(description = "ID of the task to update", example = "1") @PathVariable Long id,
      @RequestBody TaskRequest request) {
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

  @Operation(
      summary = "Get all tasks",
      description =
          "Returns a list of all tasks. "
              + "Use /filter for paginated results with title, project, date, and completion filters.")
  @GetMapping("/filter")
  public PaginationResponse<TaskResponse> filterTasks(FilterTaskDto filter, Pagination pagination) {
    return new PaginationResponse<>(taskService.filterTask(filter, pagination), pagination);
  }
}
