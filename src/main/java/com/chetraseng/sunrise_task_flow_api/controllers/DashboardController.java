package com.chetraseng.sunrise_task_flow_api.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(DashboardController.BASE_URL)
public class DashboardController {
    public static final String BASE_URL = "/api/dashboard";

    @GetMapping
    public ResponseEntity<?> getDashboardData() {
        return ResponseEntity.ok(null);
    }
}
