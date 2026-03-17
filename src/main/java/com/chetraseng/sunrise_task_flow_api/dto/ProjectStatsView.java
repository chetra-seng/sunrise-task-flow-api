package com.chetraseng.sunrise_task_flow_api.dto;

import lombok.Data;


public interface ProjectStatsView {
    String getProjectName();
    long getTaskCount();
    long getDoneCount();
}
