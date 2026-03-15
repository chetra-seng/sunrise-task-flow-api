package com.chetraseng.sunrise_task_flow_api.Services;

import com.chetraseng.sunrise_task_flow_api.dto.TaskRequest;
import com.chetraseng.sunrise_task_flow_api.dto.TaskResponse;
import com.chetraseng.sunrise_task_flow_api.exception.ResourceNotFoundException;
import com.chetraseng.sunrise_task_flow_api.mapper.TaskMapper;
import com.chetraseng.sunrise_task_flow_api.repository.ProjectRepository;
import com.chetraseng.sunrise_task_flow_api.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class TaskServiceImpl implements TaskService {

    private TaskRepository taskRepository;
    private  ProjectRepository projectRepository;
    private  TaskMapper taskMapper;
    @Override
    public List<TaskResponse> findAll() {
        return taskRepository.findAll()
                .stream()
                .map(taskMapper::toTaskResponse).toList();
    }

    @Override
    public TaskResponse findById(Long id) {
        return taskRepository.findById(id)
                .map(taskMapper::toTaskResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
    }

    @Override
    public TaskResponse create(TaskRequest request) {
        return null;
    }

    @Override
    public TaskResponse update(Long id, TaskRequest request) {
        return null;
    }

    @Override
    public void delete(Long id) {

    }

    @Override
    public List<TaskResponse> findOverdueTasks() {
        return taskRepository.findOverdueTasks(LocalDate.now()).stream().map(taskMapper::toTaskResponse).toList();
    }
}
