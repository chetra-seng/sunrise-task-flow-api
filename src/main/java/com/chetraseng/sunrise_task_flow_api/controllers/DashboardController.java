package com.chetraseng.sunrise_task_flow_api.controllers;

import com.chetraseng.sunrise_task_flow_api.dto.DashboardResponse;
import com.chetraseng.sunrise_task_flow_api.service.DashboardService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/dashboard")

public class DashboardController {
    @Autowired

    private DashboardService dashboardService;


    @GetMapping("/summary")
    public ResponseEntity<DashboardResponse> getDashboard() {
        return ResponseEntity.status(200).body(this.dashboardService.getDashboard());
    }

}
