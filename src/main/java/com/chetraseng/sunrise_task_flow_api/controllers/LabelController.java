package com.chetraseng.sunrise_task_flow_api.controllers;

import com.chetraseng.sunrise_task_flow_api.dto.LabelRequest;
import com.chetraseng.sunrise_task_flow_api.dto.LabelResponse;
import com.chetraseng.sunrise_task_flow_api.dto.TaskResponse;
import com.chetraseng.sunrise_task_flow_api.services.LabelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/labels")
@RequiredArgsConstructor
public class LabelController {

  private final LabelService labelService;

  @GetMapping
  public ResponseEntity<List<LabelResponse>> findAll() {
    return ResponseEntity.ok(labelService.findAll());
  }

  @GetMapping("/{id}")
  public ResponseEntity<LabelResponse> findById(@PathVariable Long id) {
    return ResponseEntity.ok(labelService.findById(id));
  }

  @PostMapping
  public ResponseEntity<LabelResponse> create(@RequestBody LabelRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(labelService.create(request));
  }

  @PutMapping("/{id}")
  public ResponseEntity<LabelResponse> update(
      @PathVariable Long id, @RequestBody LabelRequest request) {
    return ResponseEntity.ok(labelService.update(id, request));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    labelService.delete(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/{id}/tasks")
  public ResponseEntity<List<TaskResponse>> findTasksByLabelId(@PathVariable Long id) {
    return ResponseEntity.ok(labelService.findTasksByLabelId(id));
  }
}
