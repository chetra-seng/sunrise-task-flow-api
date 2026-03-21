package com.chetraseng.sunrise_task_flow_api.controllers;

import com.chetraseng.sunrise_task_flow_api.dto.CommentRequest;
import com.chetraseng.sunrise_task_flow_api.dto.CommentResponse;
import com.chetraseng.sunrise_task_flow_api.services.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CommentController {

  private final CommentService commentService;

  @GetMapping("/api/tasks/{taskId}/comments")
  public ResponseEntity<List<CommentResponse>> findByTaskId(@PathVariable Long taskId) {
    return ResponseEntity.ok(commentService.findByTaskId(taskId));
  }

  @PostMapping("/api/tasks/{taskId}/comments")
  public ResponseEntity<CommentResponse> create(
      @PathVariable Long taskId, @RequestBody CommentRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(commentService.create(taskId, request));
  }

  @PutMapping("/api/comments/{id}")
  public ResponseEntity<CommentResponse> update(
      @PathVariable Long id, @RequestBody CommentRequest request) {
    return ResponseEntity.ok(commentService.update(id, request));
  }

  @DeleteMapping("/api/comments/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    commentService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
