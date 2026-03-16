package com.chetraseng.sunrise_task_flow_api.Services;

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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final ProjectMapper projectMapper;
    private final TaskMapper taskMapper;

    @Override
    public List<ProjectResponse> findAll() {
        return projectRepository.findAll().stream().map(projectMapper::toProjectResponse).collect(Collectors.toList());
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
        ProjectModel project = projectMapper.toModel(request);
        return projectMapper.toProjectResponse((projectRepository.save(project)));
    }

    @Override
    public ProjectResponse update(Long id, ProjectRequest request) {
        ProjectModel existingProject = projectRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Project not found with id: "+id));
        projectMapper.updateModel(existingProject, request);
        return projectMapper.toProjectResponse(projectRepository.save(existingProject));
    }

    @Override
    public void delete(Long id) {
        if (!projectRepository.existsById(id)) {
            throw new ResourceNotFoundException("Project not found with id: "+id);
        }
        List<TaskModel> tasks = taskRepository.findByProjectId(id);
        taskRepository.deleteAll(tasks);
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
