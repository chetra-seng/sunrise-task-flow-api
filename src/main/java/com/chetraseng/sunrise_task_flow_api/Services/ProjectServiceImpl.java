package com.chetraseng.sunrise_task_flow_api.Services;

import com.chetraseng.sunrise_task_flow_api.dto.ProjectRequest;
import com.chetraseng.sunrise_task_flow_api.dto.ProjectResponse;
import com.chetraseng.sunrise_task_flow_api.dto.TaskResponse;
import com.chetraseng.sunrise_task_flow_api.exception.ResourceNotFoundException;
import com.chetraseng.sunrise_task_flow_api.mapper.ProjectMapper;
import com.chetraseng.sunrise_task_flow_api.mapper.TaskMapper;
import com.chetraseng.sunrise_task_flow_api.model.ProjectModel;
import com.chetraseng.sunrise_task_flow_api.repository.ProjectRepository;
import com.chetraseng.sunrise_task_flow_api.repository.TaskRepository;
import jakarta.transaction.Transactional;

import java.util.List;

public class ProjectServiceImpl implements ProjectService {
    private ProjectRepository projectRepository;
    private TaskRepository taskRepository;
    private ProjectMapper projectMapper;
    private TaskMapper taskMapper;

    @Override
    public List<ProjectResponse> findAll() {
        return projectRepository.findAll().stream().map(projectMapper::toProjectResponse).toList();
    }

    @Override
    public ProjectResponse findById(Long id) {
        return projectRepository.findById(id)
                .map(projectMapper::toProjectResponse)
                .orElseThrow(()-> new ResourceNotFoundException("Project not found with id: "+id));
    }

    @Override
    @Transactional
    public ProjectResponse create(ProjectRequest request) {
        return null;
    }

    @Override
    public ProjectResponse update(Long id, ProjectRequest request) {
        ProjectModel existingProject = projectRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Project not found with id: "+id));
        existingProject.setName(request.getName());
        return projectMapper.toProjectResponse(projectRepository.save(existingProject));
    }

    @Override
    public void delete(Long id) {
        if (!projectRepository.existsById(id)) {
            throw new ResourceNotFoundException("Project not found with id: "+id);
        }
        projectRepository.deleteById(id);
    }

    @Override
    public List<TaskResponse> findTasksByProjectId(Long id) {
       if (!projectRepository.existsById(id)) {
           throw new ResourceNotFoundException("Project not found with id: "+id);
       }
        return taskRepository.findByProjectId(id).stream().map(taskMapper::toTaskResponse).toList();
    }
}
