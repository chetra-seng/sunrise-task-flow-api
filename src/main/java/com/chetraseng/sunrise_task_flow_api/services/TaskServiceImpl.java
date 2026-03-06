package com.chetraseng.sunrise_task_flow_api.services;

import com.chetraseng.sunrise_task_flow_api.dto.TaskRequest;
import com.chetraseng.sunrise_task_flow_api.dto.TaskResponse;
import com.chetraseng.sunrise_task_flow_api.exception.ResourceNotFoundException;
import com.chetraseng.sunrise_task_flow_api.mapper.TaskMapper;
import com.chetraseng.sunrise_task_flow_api.model.Priority;
import com.chetraseng.sunrise_task_flow_api.model.ProjectModel;
import com.chetraseng.sunrise_task_flow_api.model.TaskModel;
import com.chetraseng.sunrise_task_flow_api.model.TaskStatus;
import com.chetraseng.sunrise_task_flow_api.repository.ProjectRepository;
import com.chetraseng.sunrise_task_flow_api.repository.TaskRepository;
import com.chetraseng.sunrise_task_flow_api.specification.TaskSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
  private final TaskRepository taskRepository;
  private final ProjectRepository projectRepository;
  private final TaskMapper taskMapper;

  @Override
  @Transactional(readOnly = true)
  public List<TaskResponse> findAll(Boolean completed) {
    List<TaskModel> tasks;
    if (completed != null) {
      tasks = taskRepository.findAllByCompleted(completed);
    } else {
      tasks = taskRepository.findAll();
    }
    return tasks.stream().map(taskMapper::toTaskResponse).toList();
  }

  @Override
  @Transactional(readOnly = true)
  public TaskResponse findById(Long id) {
    return taskRepository
        .findById(id)
        .map(taskMapper::toTaskResponse)
        .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
  }

  @Override
  @Transactional
  public TaskResponse create(TaskRequest request) {
    TaskModel task = TaskModel.builder()
        .title(request.getTitle())
        .description(request.getDescription())
        .build();

    if (request.getPriority() != null) {
      task.setPriority(Priority.valueOf(request.getPriority()));
    }
    if (request.getStatus() != null) {
      task.setStatus(TaskStatus.valueOf(request.getStatus()));
    }
    if (request.getDueDate() != null) {
      task.setDueDate(request.getDueDate());
    }
    if (request.getProjectId() != null) {
      ProjectModel project = projectRepository.findById(request.getProjectId())
          .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + request.getProjectId()));
      task.setProject(project);
    }

    return taskMapper.toTaskResponse(taskRepository.save(task));
  }

  @Override
  @Transactional
  public TaskResponse update(Long id, TaskRequest request) {
    TaskModel task = taskRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));

    task.setTitle(request.getTitle());
    task.setDescription(request.getDescription());

    if (request.getPriority() != null) {
      task.setPriority(Priority.valueOf(request.getPriority()));
    }
    if (request.getStatus() != null) {
      task.setStatus(TaskStatus.valueOf(request.getStatus()));
    }
    if (request.getDueDate() != null) {
      task.setDueDate(request.getDueDate());
    }
    if (request.getProjectId() != null) {
      ProjectModel project = projectRepository.findById(request.getProjectId())
          .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + request.getProjectId()));
      task.setProject(project);
    }

    return taskMapper.toTaskResponse(taskRepository.save(task));
  }

  @Override
  @Transactional
  public TaskResponse complete(Long id) {
    TaskModel task = taskRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
    task.setCompleted(true);
    task.setStatus(TaskStatus.DONE);
    return taskMapper.toTaskResponse(taskRepository.save(task));
  }

  @Override
  @Transactional
  public void delete(Long id) {
    if (!taskRepository.existsById(id)) {
      throw new ResourceNotFoundException("Task not found with id: " + id);
    }
    taskRepository.deleteById(id);
  }

  @Override
  @Transactional(readOnly = true)
  public List<TaskResponse> filterTask(Boolean completed, String title) {
    return taskRepository.findAllByCompletedAndTitleContainingIgnoreCase(completed, title).stream()
        .map(taskMapper::toTaskResponse)
        .toList();
  }

  @Override
  @Transactional(readOnly = true)
  public List<TaskResponse> findByStatus(TaskStatus status) {
    return taskRepository.findAllByStatus(status).stream()
        .map(taskMapper::toTaskResponse)
        .toList();
  }

  @Override
  @Transactional(readOnly = true)
  public List<TaskResponse> findByPriority(Priority priority) {
    return taskRepository.findAllByPriority(priority).stream()
        .map(taskMapper::toTaskResponse)
        .toList();
  }

  @Override
  @Transactional(readOnly = true)
  public Page<TaskResponse> findAll(Pageable pageable) {
    return taskRepository.findAll(pageable).map(taskMapper::toTaskResponse);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<TaskResponse> searchTasks(Boolean completed, String keyword, Long projectId,
                                         TaskStatus status, Priority priority, Pageable pageable) {
    Specification<TaskModel> spec = Specification.where(
        (Specification<TaskModel>) null);

    if (completed != null) {
      spec = spec.and(TaskSpecification.isCompleted(completed));
    }
    if (keyword != null) {
      spec = spec.and(TaskSpecification.titleContains(keyword));
    }
    if (projectId != null) {
      spec = spec.and(TaskSpecification.inProject(projectId));
    }
    if (status != null) {
      spec = spec.and(TaskSpecification.hasStatus(status));
    }
    if (priority != null) {
      spec = spec.and(TaskSpecification.hasPriority(priority));
    }

    return taskRepository.findAll(spec, pageable).map(taskMapper::toTaskResponse);
  }
}
