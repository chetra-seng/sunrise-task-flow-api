package com.chetraseng.sunrise_task_flow_api.dto;

import java.util.List;

public record DashboardResponse(
        long totalTasks,
        long todoCount,
        long inProgressCount,
        long doneCount,
        long overdueCount,
        List<ProjectStatsView> projectStats
) {}
