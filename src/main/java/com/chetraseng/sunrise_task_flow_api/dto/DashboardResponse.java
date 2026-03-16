package com.chetraseng.sunrise_task_flow_api.dto;

import lombok.Builder;

import java.util.List;
@Builder
public record DashboardResponse(
        long totalTasks,
        long todoCount,
        long inProgressCount,
        long doneCount,
        long overdueCount,
        List<ProjectStatsView> projectStats
        ) {}
