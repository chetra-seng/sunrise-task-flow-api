package com.chetraseng.sunrise_task_flow_api.controllers;

import com.chetraseng.sunrise_task_flow_api.Services.CommentService;
import com.chetraseng.sunrise_task_flow_api.dto.CommentRequest;
import com.chetraseng.sunrise_task_flow_api.dto.CommentResponse;
import com.chetraseng.sunrise_task_flow_api.model.CommentModel;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CommentController {
    private final CommentService commentService;

    @GetMapping("/tasks/{taskId}/comments")
    public ResponseEntity<List<CommentResponse>> findByTaskId(@PathVariable Long taskId){
        return ResponseEntity.ok(commentService.findByTaskId(taskId));
    }
    @PostMapping("/tasks/{taskId}/comments")
    public ResponseEntity<CommentResponse> create(@PathVariable Long taskId, @RequestBody CommentRequest request){
        CommentResponse created = commentService.create(taskId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/comments/{id}")
    public ResponseEntity<CommentResponse> update(@PathVariable Long id, @RequestBody CommentRequest request){
        return ResponseEntity.ok(commentService.update(id,request));
    }

    @DeleteMapping("/comments/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        commentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
