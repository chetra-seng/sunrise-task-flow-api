package com.chetraseng.sunrise_task_flow_api.services;

import com.chetraseng.sunrise_task_flow_api.dto.ProjectRequest;
import com.chetraseng.sunrise_task_flow_api.dto.ProjectResponse;
import com.chetraseng.sunrise_task_flow_api.exception.ResourceNotFoundException;
import com.chetraseng.sunrise_task_flow_api.mapper.ProjectMapper;
import com.chetraseng.sunrise_task_flow_api.model.ProjectModel;
import com.chetraseng.sunrise_task_flow_api.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {
  private final ProjectRepository projectRepository;
  private final ProjectMapper projectMapper;

  @Override
  @Transactional(readOnly = true)
  public List<ProjectResponse> findAll() {
    return projectRepository.findAll().stream()
        .map(projectMapper::toProjectResponse)
        .toList();
  }

  @Override
  @Transactional(readOnly = true)
  public ProjectResponse findById(Long id) {
    return projectRepository.findById(id)
        .map(projectMapper::toProjectResponse)
        .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + id));
  }

  @Override
  @Transactional
  public ProjectResponse create(ProjectRequest request) {
    ProjectModel project = ProjectModel.builder()
        .name(request.getName())
        .description(request.getDescription())
        .build();
    return projectMapper.toProjectResponse(projectRepository.save(project));
  }

  @Override
  @Transactional
  public ProjectResponse update(Long id, ProjectRequest request) {
    ProjectModel project = projectRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + id));
    project.setName(request.getName());
    project.setDescription(request.getDescription());
    return projectMapper.toProjectResponse(projectRepository.save(project));
  }

  @Override
  @Transactional
  public void delete(Long id) {
    if (!projectRepository.existsById(id)) {
      throw new ResourceNotFoundException("Project not found with id: " + id);
    }
    projectRepository.deleteById(id);
  }
}
