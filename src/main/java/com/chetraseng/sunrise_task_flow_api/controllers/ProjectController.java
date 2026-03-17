package com.chetraseng.sunrise_task_flow_api.controllers;

import com.chetraseng.sunrise_task_flow_api.dto.ProjectRequest;
import com.chetraseng.sunrise_task_flow_api.dto.ProjectResponse;
import com.chetraseng.sunrise_task_flow_api.service.ProjectService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Data
@RequestMapping("/api/projects")
public class ProjectController {
    @Autowired
    private ProjectService projectService;

    @GetMapping
    public List<ProjectResponse> findAll() {
        return this.projectService.findAll();

    }
    @PostMapping
    public ResponseEntity<ProjectResponse> create(@RequestBody ProjectRequest projectRequest) {
         return ResponseEntity.status(201).body(this.projectService.create(projectRequest));

    }

  @PutMapping("/{id}")
  public ResponseEntity<ProjectResponse> update(@PathVariable Long id, @RequestBody ProjectRequest projectRequest) {
    return ResponseEntity.status(202).body(this.projectService.update(id, projectRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ProjectResponse> delete(@PathVariable Long id) {
        projectService.delete(id);
        return ResponseEntity.status(204).build();

    }
    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponse> findById(@PathVariable Long id) {
        return ResponseEntity.status(200).body(this.projectService.findById(id));
    }
    @GetMapping("/name")
    public ResponseEntity<List<ProjectResponse>> findByName(@RequestParam String name) {
        return ResponseEntity.status(200).body(Collections.singletonList(this.projectService.findByName(name)));
    }

    @GetMapping("/findById")
    public ResponseEntity<ProjectResponse> findTaskByProjectId(@RequestParam long id) {
        return ResponseEntity.status(200).body(this.projectService.findById(id));
    }

}
