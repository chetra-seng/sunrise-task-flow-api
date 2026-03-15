package com.chetraseng.sunrise_task_flow_api.service;

import com.chetraseng.sunrise_task_flow_api.dto.TaskRequest;
import com.chetraseng.sunrise_task_flow_api.dto.TaskResponse;
import com.chetraseng.sunrise_task_flow_api.mapper.TaskMapper;
import com.chetraseng.sunrise_task_flow_api.model.ProjectModel;
import com.chetraseng.sunrise_task_flow_api.model.TaskModel;
import com.chetraseng.sunrise_task_flow_api.repository.TaskRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Data
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;

    @Autowired
    private final TaskMapper taskMapper;
    @Autowired
    private ProjectModelRepository projectModelRepository;

    @Override
    public List<TaskResponse> findAll() {
        List<TaskModel> tasks = taskRepository.findAll();
        return tasks.stream().map(taskMapper::toResponse).toList();

        }

    @Override
    public TaskResponse findById(Long id) {
        return null;
    }

    @Override
    public TaskResponse create(TaskRequest request) {
        TaskModel task = taskMapper.toEntity(request);
        if (request.getProject_id() != null) {
            ProjectModel project = projectModelRepository.findById(request.getProject_id()).orElseThrow(() -> new RuntimeException("Project not found"));
            task.setProject(project);
        }
            return taskMapper.toResponse(taskRepository.save(task));
        }



    @Override
    public TaskResponse update(Long id, TaskRequest request) {
        return null;
    }

    @Override
    public void delete(Long id) {

    }
}
