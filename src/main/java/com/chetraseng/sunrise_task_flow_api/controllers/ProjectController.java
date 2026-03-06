package com.chetraseng.sunrise_task_flow_api.controllers;

import com.chetraseng.sunrise_task_flow_api.dto.ProjectRequest;
import com.chetraseng.sunrise_task_flow_api.dto.ProjectResponse;
import com.chetraseng.sunrise_task_flow_api.services.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/projects")
public class ProjectController {
  private final ProjectService projectService;

  @GetMapping
  public ResponseEntity<List<ProjectResponse>> getAllProjects() {
    return ResponseEntity.ok(projectService.findAll());
  }

  @GetMapping("/{id}")
  public ResponseEntity<ProjectResponse> getProjectById(@PathVariable Long id) {
    return ResponseEntity.ok(projectService.findById(id));
  }

  @PostMapping
  public ResponseEntity<ProjectResponse> createProject(@RequestBody ProjectRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(projectService.create(request));
  }

  @PutMapping("/{id}")
  public ResponseEntity<ProjectResponse> updateProject(@PathVariable Long id,
                                                        @RequestBody ProjectRequest request) {
    return ResponseEntity.ok(projectService.update(id, request));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
    projectService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
