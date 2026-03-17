package com.chetraseng.sunrise_task_flow_api.controllers;
import com.chetraseng.sunrise_task_flow_api.dto.CommentRequest;
import com.chetraseng.sunrise_task_flow_api.dto.CommentResponse;
import com.chetraseng.sunrise_task_flow_api.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tasks")
public class CommentController {
    @Autowired
    private CommentService commentService;




    @GetMapping("/{id}/comments")
    public List<CommentResponse> findByTaskId(@PathVariable Long id) {
        return this.commentService.findByTaskId(id);

    }
    @PostMapping("/{id}/comments")
    public ResponseEntity <CommentResponse> create(@PathVariable Long id, @RequestBody CommentRequest commentRequest) {
    return ResponseEntity.ok(this.commentService.create(id, commentRequest));

    }
    @PutMapping("/{id}/comments")
    public ResponseEntity <CommentResponse> update(@PathVariable Long id, @RequestBody CommentRequest commentRequest) {
    return ResponseEntity.ok(this.commentService.update(id, commentRequest));
    }
    public ResponseEntity<CommentResponse> delete(@PathVariable Long id) {
        commentService.delete(id);
        return ResponseEntity.status(204).build();

    }
}
