package com.chetraseng.sunrise_task_flow_api.service;

import com.chetraseng.sunrise_task_flow_api.dto.ProjectRequest;
import com.chetraseng.sunrise_task_flow_api.dto.ProjectResponse;
import com.chetraseng.sunrise_task_flow_api.dto.TaskResponse;
import com.chetraseng.sunrise_task_flow_api.exception.ResourceNotFoundException;
import com.chetraseng.sunrise_task_flow_api.mapper.ProjectMapper;
import com.chetraseng.sunrise_task_flow_api.mapper.TaskMapper;
import com.chetraseng.sunrise_task_flow_api.model.ProjectModel;
import com.chetraseng.sunrise_task_flow_api.repository.ProjectRepository;
import com.chetraseng.sunrise_task_flow_api.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor

public class ProjectServiceImpl implements ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;



    @Override
    public List<ProjectResponse> findAll() {
        List<ProjectModel> projectModels = projectRepository.findAll();
        return projectModels.stream().map(projectMapper::toProjectResponse).toList();
    }

    @Override
    public ProjectResponse findById(Long id) {
        return projectRepository.findById(id).map(projectMapper::toProjectResponse).orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + id));

    }
    @Override
    public ProjectResponse findByName(String name) {
        return projectRepository.findByName(name).map(projectMapper::toProjectResponse).orElseThrow(() -> new ResourceNotFoundException("Project not found with name: " + name));
    }

    @Override
    public ProjectResponse create(ProjectRequest request) {
        ProjectModel projectModel = new ProjectModel();
        projectModel.setName(request.getName());
        return projectMapper.toProjectResponse(projectRepository.save(projectModel));
    }

    @Override
    public ProjectResponse update(Long id, ProjectRequest request) {
        ProjectModel project = projectRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + id));
        project.setName(request.getName());
        return projectMapper.toProjectResponse(projectRepository.save(project));
    }

    @Override
    public void delete(Long id) {
        if(!projectRepository.existsById(id)){
            throw new ResourceNotFoundException("Project not found with id: " + id);
        }
        projectRepository.deleteById(id);

    }


    @Override
    public List<TaskResponse> findTaskByProjectId(Long id) {
        if (!projectRepository.existsById(id)) {
            throw new ResourceNotFoundException("Project not found with id: " + id);
        }
        return taskRepository.findByProjectId(id)
                .stream()
                .map(taskMapper::toResponse)
                .toList();
    }

}
