package com.chetraseng.sunrise_task_flow_api.controllers;

import com.chetraseng.sunrise_task_flow_api.Services.DashboardService;
import com.chetraseng.sunrise_task_flow_api.dto.DashboardResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/dashboard")
public class DashboardController {
    private final DashboardService dashboardService;
    @GetMapping("/api/dashboard/summary")
    public ResponseEntity<DashboardResponse> gerSummary(){
        return ResponseEntity.ok(dashboardService.getSummary());
    }
}
