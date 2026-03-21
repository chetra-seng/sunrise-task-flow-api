package com.chetraseng.sunrise_task_flow_api.services;

import com.chetraseng.sunrise_task_flow_api.dto.ProjectRequest;
import com.chetraseng.sunrise_task_flow_api.dto.ProjectResponse;
import com.chetraseng.sunrise_task_flow_api.dto.TaskResponse;
import com.chetraseng.sunrise_task_flow_api.exception.ResourceNotFoundException;
import com.chetraseng.sunrise_task_flow_api.mapper.ProjectMapper;
import com.chetraseng.sunrise_task_flow_api.mapper.TaskMapper;
import com.chetraseng.sunrise_task_flow_api.model.ProjectModel;
import com.chetraseng.sunrise_task_flow_api.model.TaskModel;
import com.chetraseng.sunrise_task_flow_api.repository.ProjectRepository;
import com.chetraseng.sunrise_task_flow_api.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

  private final ProjectRepository projectRepository;
  private final TaskRepository taskRepository;
  private final ProjectMapper projectMapper;
  private final TaskMapper taskMapper;

  @Override
  public List<ProjectResponse> findAll() {
    return projectRepository.findAll().stream().map(projectMapper::toProjectResponse).toList();
  }

  @Override
  public ProjectResponse findById(Long id) {
    ProjectModel project =
        projectRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Project not found: " + id));
    return projectMapper.toProjectResponse(project);
  }

  @Override
  public ProjectResponse create(ProjectRequest request) {
    ProjectModel project = new ProjectModel();
    project.setName(request.getName());
    return projectMapper.toProjectResponse(projectRepository.save(project));
  }

  @Override
  public ProjectResponse update(Long id, ProjectRequest request) {
    ProjectModel project =
        projectRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Project not found: " + id));
    project.setName(request.getName());
    return projectMapper.toProjectResponse(projectRepository.save(project));
  }

  @Override
  public void delete(Long id) {
    ProjectModel project =
        projectRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Project not found: " + id));
    // Null out project reference in tasks to avoid FK violation
    List<TaskModel> tasks = new java.util.ArrayList<>(project.getTasks());
    tasks.forEach(task -> task.setProject(null));
    taskRepository.saveAll(tasks);
    projectRepository.deleteById(id);
  }

  @Override
  public List<TaskResponse> findTasksByProjectId(Long id) {
    projectRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Project not found: " + id));
    return taskRepository.findByProjectId(id).stream().map(taskMapper::toTaskResponse).toList();
  }
}
