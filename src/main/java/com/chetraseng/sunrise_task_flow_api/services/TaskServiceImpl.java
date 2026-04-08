package com.chetraseng.sunrise_task_flow_api.services;

import com.chetraseng.sunrise_task_flow_api.dto.FilterTaskDto;
import com.chetraseng.sunrise_task_flow_api.dto.Pagination;
import com.chetraseng.sunrise_task_flow_api.dto.PaginationResponse;
import com.chetraseng.sunrise_task_flow_api.dto.TaskRequest;
import com.chetraseng.sunrise_task_flow_api.dto.TaskResponse;
import com.chetraseng.sunrise_task_flow_api.exception.ResourceNotFoundException;
import com.chetraseng.sunrise_task_flow_api.mapper.TaskMapper;
import com.chetraseng.sunrise_task_flow_api.model.LabelModel;
import com.chetraseng.sunrise_task_flow_api.model.ProjectModel;
import com.chetraseng.sunrise_task_flow_api.model.TaskModel;
import com.chetraseng.sunrise_task_flow_api.model.TaskStatus;
import com.chetraseng.sunrise_task_flow_api.repository.LabelRepository;
import com.chetraseng.sunrise_task_flow_api.repository.ProjectRepository;
import com.chetraseng.sunrise_task_flow_api.repository.TaskRepository;
import com.chetraseng.sunrise_task_flow_api.security.SecurityUtils;
import com.chetraseng.sunrise_task_flow_api.spec.TaskSpec;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

  private final TaskRepository taskRepository;
  private final ProjectRepository projectRepository;
  private final LabelRepository labelRepository;
  private final TaskMapper taskMapper;
  private final SecurityUtils securityUtils;


  @Override
  public List<TaskResponse> findAll() {
    return taskRepository.findAll().stream().map(taskMapper::toTaskResponse).toList();
  }



  @Override
  public TaskResponse findById(Long id) {
    TaskModel task =
        taskRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Task not found: " + id));
    return taskMapper.toTaskResponse(task);
  }

  @Override
  public TaskResponse create(TaskRequest request) {
    TaskModel task = new TaskModel();
    task.setTitle(request.getTitle());
    task.setDescription(request.getDescription());
    if (request.getStatus() != null) task.setStatus(request.getStatus());
    if (request.getPriority() != null) task.setPriority(request.getPriority());
    task.setDueDate(request.getDueDate());
    if (request.getProjectId() != null) {
      ProjectModel project =
          projectRepository
              .findById(request.getProjectId())
              .orElseThrow(
                  () ->
                      new ResourceNotFoundException(
                          "Project not found: " + request.getProjectId()));
      task.setProject(project);
    }
    return taskMapper.toTaskResponse(taskRepository.save(task));
  }

  @Override
  public TaskResponse update(Long id, TaskRequest request) {
    TaskModel task =
        taskRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Task not found: " + id));
    task.setTitle(request.getTitle());
    task.setDescription(request.getDescription());
    if (request.getStatus() != null) task.setStatus(request.getStatus());
    if (request.getPriority() != null) task.setPriority(request.getPriority());
    task.setDueDate(request.getDueDate());
    return taskMapper.toTaskResponse(taskRepository.save(task));
  }

  @Override
  public void delete(Long id) {
    taskRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Task not found: " + id));
    taskRepository.deleteById(id);
  }

  @Override
  public List<TaskResponse> findOverdueTasks() {
    return taskRepository.findOverdueTasks(LocalDate.now()).stream()
        .map(taskMapper::toTaskResponse)
        .toList();
  }

  @Override
  public PaginationResponse<TaskResponse> filterTasks(FilterTaskDto filter, Pagination pagination) {
    Specification<TaskModel> spec = Specification.unrestricted();
    if (filter.getTitle() != null) spec = spec.and(TaskSpec.containsTitle(filter.getTitle()));
    if (filter.getProjectId() != null)
      spec = spec.and(TaskSpec.equalProjectId(filter.getProjectId()));
    if (filter.getStatus() != null) spec = spec.and(TaskSpec.hasStatus(filter.getStatus()));
    if (filter.getPriority() != null) spec = spec.and(TaskSpec.hasPriority(filter.getPriority()));
    if (filter.getDueBefore() != null) spec = spec.and(TaskSpec.dueBefore(filter.getDueBefore()));
    if (filter.getLabelId() != null) spec = spec.and(TaskSpec.hasLabel(filter.getLabelId()));
    Pageable pageable =
        PageRequest.of(pagination.getPage(), pagination.getSize(), Sort.by("id").descending());
    Page<TaskModel> page = taskRepository.findAll(spec, pageable);

    List<TaskResponse> data =
        page.getContent().stream().map(taskMapper::toTaskResponse).toList();

    securityUtils.getCurrentUser()
            .ifPresent(user -> page.getContent().forEach(task -> task.setOwner(user)));

    Pagination meta = new Pagination();
    meta.setPage(pagination.getPage());
    meta.setSize(pagination.getSize());
    meta.setTotal(page.getTotalElements());
    meta.setTotalPage(page.getTotalPages());

    return new PaginationResponse<>(data, meta);
  }

  @Override
  public TaskResponse updateStatus(Long id, TaskStatus status) {
    TaskModel task =
        taskRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Task not found: " + id));
    task.setStatus(status);
    return taskMapper.toTaskResponse(taskRepository.save(task));
  }

  @Override
  public TaskResponse addLabel(Long taskId, Long labelId) {
    TaskModel task =
        taskRepository
            .findById(taskId)
            .orElseThrow(() -> new ResourceNotFoundException("Task not found: " + taskId));
    LabelModel label =
        labelRepository
            .findById(labelId)
            .orElseThrow(() -> new ResourceNotFoundException("Label not found: " + labelId));
    task.getLabels().add(label);
    return taskMapper.toTaskResponse(taskRepository.save(task));
  }

  @Override
  public TaskResponse removeLabel(Long taskId, Long labelId) {
    TaskModel task =
        taskRepository
            .findById(taskId)
            .orElseThrow(() -> new ResourceNotFoundException("Task not found: " + taskId));
    LabelModel label =
        labelRepository
            .findById(labelId)
            .orElseThrow(() -> new ResourceNotFoundException("Label not found: " + labelId));
    task.getLabels().remove(label);
    return taskMapper.toTaskResponse(taskRepository.save(task));
  }

}
