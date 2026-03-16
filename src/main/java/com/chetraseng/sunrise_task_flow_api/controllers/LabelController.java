package com.chetraseng.sunrise_task_flow_api.controllers;

import com.chetraseng.sunrise_task_flow_api.Services.LabelService;
import com.chetraseng.sunrise_task_flow_api.dto.LabelRequest;
import com.chetraseng.sunrise_task_flow_api.dto.LabelResponse;
import com.chetraseng.sunrise_task_flow_api.dto.TaskResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/labels")
public class LabelController {
    private final LabelService labelService;

    @GetMapping
    public ResponseEntity<List<LabelResponse>> findAll(){
        return ResponseEntity.ok(labelService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LabelResponse> findById(@PathVariable Long id){
        return ResponseEntity.ok(labelService.findById(id));
    }

    @PostMapping
    public ResponseEntity<LabelResponse> createLabel(@RequestBody LabelRequest labelRequest){
        return ResponseEntity.status(HttpStatus.CREATED).body(labelService.create(labelRequest));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LabelResponse> updateLabel(@PathVariable Long id,@RequestBody LabelRequest labelRequest){
        return  ResponseEntity.ok(labelService.update(id, labelRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLabel(@PathVariable Long id){
        labelService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/tasks")
    public ResponseEntity<List<TaskResponse>> findTaskByLabelId(@PathVariable Long id){
        return ResponseEntity.ok(labelService.findTasksByLabelId(id));
    }
}
