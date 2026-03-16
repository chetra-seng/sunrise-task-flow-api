package com.chetraseng.sunrise_task_flow_api.controllers;

import com.chetraseng.sunrise_task_flow_api.Services.ProjectService;
import com.chetraseng.sunrise_task_flow_api.dto.ProjectRequest;
import com.chetraseng.sunrise_task_flow_api.dto.ProjectResponse;
import com.chetraseng.sunrise_task_flow_api.dto.TaskResponse;
import com.chetraseng.sunrise_task_flow_api.model.ProjectModel;
import com.chetraseng.sunrise_task_flow_api.repository.ProjectRepository;
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

    @GetMapping("/api/projects")
   public ResponseEntity<List<ProjectResponse>> findAll(){
        return ResponseEntity.ok(projectService.findAll());
    }

    @GetMapping("/api/projects/{id}")
    public ResponseEntity<ProjectResponse> findById(@PathVariable Long id){
        return ResponseEntity.ok(projectService.findById(id));
    }

    @PostMapping("/api/projects")
    public ResponseEntity<ProjectResponse> createProject(@RequestBody ProjectRequest projectRequest){
        return ResponseEntity.status(HttpStatus.CREATED).body(projectService.create(projectRequest));
    }

    @PutMapping("/api/projects/{id}")
    public ResponseEntity<ProjectResponse> updateProject(@PathVariable Long id, @RequestBody ProjectRequest projectRequest){
        return ResponseEntity.ok(projectService.update(id, projectRequest));
    }

    @DeleteMapping("/api/projects/{id}")
    public ResponseEntity<Void>  deleteProject(@PathVariable Long id){
        projectService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/api/projects/{id}/tasks")
    public ResponseEntity<List<TaskResponse>> findTasksByProjectId(@PathVariable Long id){
        return ResponseEntity.ok(projectService.findTasksByProjectId(id));
    }

}
