package com.chetraseng.sunrise_task_flow_api.service;

import com.chetraseng.sunrise_task_flow_api.dto.ProjectRequest;
import com.chetraseng.sunrise_task_flow_api.dto.ProjectResponse;
import com.chetraseng.sunrise_task_flow_api.dto.TaskResponse;

import java.util.List;

public interface ProjectService {
List<ProjectResponse> findAll();
ProjectResponse findById(Long id);
ProjectResponse findByName(String name);
ProjectResponse create(ProjectRequest request);
ProjectResponse update(Long id,ProjectRequest request);
void delete(Long id);
List<TaskResponse> findTaskByProjectId(Long id);


}
