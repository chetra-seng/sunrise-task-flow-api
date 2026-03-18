package com.chetraseng.sunrise_task_flow_api.controllers;

import com.chetraseng.sunrise_task_flow_api.dto.LabelRequest;
import com.chetraseng.sunrise_task_flow_api.dto.LabelResponse;
import com.chetraseng.sunrise_task_flow_api.service.LabelService;
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
    public ResponseEntity<List<LabelResponse>> getAllLabels() {
        return ResponseEntity.ok(labelService.findAll());
    }


    @GetMapping("/{id}")
    public ResponseEntity<LabelResponse> getLabelById(@PathVariable Long id) {
        return ResponseEntity.ok(labelService.findById(id));
    }


    @PostMapping
    public ResponseEntity<LabelResponse> createLabel(@RequestBody LabelRequest request) {
        LabelResponse createdLabel = labelService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdLabel);
    }


    @PutMapping("/{id}")
    public ResponseEntity<LabelResponse> updateLabel(@PathVariable Long id, @RequestBody LabelRequest request) {
        return ResponseEntity.ok(labelService.update(id, request));
    }
}
