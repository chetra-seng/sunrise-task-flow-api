package com.chetraseng.sunrise_task_flow_api.services;

import com.chetraseng.sunrise_task_flow_api.dto.TaskResponse;
import com.chetraseng.sunrise_task_flow_api.mapper.TaskMapper;
import com.chetraseng.sunrise_task_flow_api.model.Task;
import com.chetraseng.sunrise_task_flow_api.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
  private final TaskRepository taskRepository;
  private final TaskMapper taskMapper;

  @Override
  public List<TaskResponse> findAll() {
    return taskRepository.findAll().stream().map(taskMapper::toTaskResponse).toList();
  }

  @Override
  public Optional<TaskResponse> findById(Long id) {
    return taskRepository.findById(id).map(taskMapper::toTaskResponse);
  }

  @Override
  public TaskResponse create(String title, String description) {
    Task task = new Task();
    task.setTitle(title);
    task.setDescription(description);
    Task savedTask = taskRepository.save(task);
    return taskMapper.toTaskResponse(savedTask);
  }

  @Override
  public Optional<TaskResponse> update(Long id, String title, String description) {
    Optional<Task> optionalTask = taskRepository.findById(id);

    if (optionalTask.isEmpty()) {
      return Optional.empty();
    }

    Task task = optionalTask.get();
    task.setTitle(title);
    task.setDescription(description);
    Task savedTask = taskRepository.save(task);
    TaskResponse response = taskMapper.toTaskResponse(savedTask);
    return Optional.of(response);
  }

  @Override
  public Optional<TaskResponse> complete(Long id) {
    return Optional.empty();
  }

  @Override
  public boolean delete(Long id) {
    return false;
  }
}
