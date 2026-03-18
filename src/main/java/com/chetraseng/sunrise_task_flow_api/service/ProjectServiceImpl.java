package com.chetraseng.sunrise_task_flow_api.service;

import com.chetraseng.sunrise_task_flow_api.dto.ProjectRequest;
import com.chetraseng.sunrise_task_flow_api.dto.ProjectResponse;
import com.chetraseng.sunrise_task_flow_api.dto.TaskRequest;
import com.chetraseng.sunrise_task_flow_api.dto.TaskResponse;
import com.chetraseng.sunrise_task_flow_api.exception.ResourceNotFoundException;
import com.chetraseng.sunrise_task_flow_api.mapper.ProjectMapper;
import com.chetraseng.sunrise_task_flow_api.mapper.TaskMapper;
import com.chetraseng.sunrise_task_flow_api.model.ProjectModel;
import com.chetraseng.sunrise_task_flow_api.model.TaskModel;
import com.chetraseng.sunrise_task_flow_api.repository.ProjectRepository;
import com.chetraseng.sunrise_task_flow_api.repository.TaskRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService{
    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final TaskMapper taskMapper;
    private final ProjectMapper projectMapper;

    @Override
    public List<ProjectResponse> findAll() {
        return projectRepository.findAll().stream()
                .map(projectMapper::toProjectResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ProjectResponse findById(Long id) {
       ProjectModel project = projectRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Project not found with id: "+id));
       return projectMapper.toProjectResponse(project);
    }


    @Override
    @Transactional
    public ProjectResponse create(ProjectRequest request) {
        ProjectModel project = new ProjectModel();
        project.setName(request.getName());
        return projectMapper.toProjectResponse(projectRepository.save(project));
    }

    @Override
    @Transactional
    public ProjectResponse update(Long id, ProjectRequest request) {
        ProjectModel project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + id));

        project.setName(request.getName());
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

    @Override
    public List<TaskResponse> findTasksByProjectId(Long id) {

        if (!projectRepository.existsById(id)) {
            throw new ResourceNotFoundException("Project not found with id: " + id);
        }
        return taskRepository.findByProjectId(id).stream()
                .map(taskMapper::toTaskResponse)
                .collect(Collectors.toList());
    }
}
