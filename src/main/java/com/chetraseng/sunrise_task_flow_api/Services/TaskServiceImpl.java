package com.chetraseng.sunrise_task_flow_api.Services;

import com.chetraseng.sunrise_task_flow_api.dto.*;
import com.chetraseng.sunrise_task_flow_api.exception.ResourceNotFoundException;
import com.chetraseng.sunrise_task_flow_api.mapper.TaskMapper;
import com.chetraseng.sunrise_task_flow_api.model.LabelModel;
import com.chetraseng.sunrise_task_flow_api.model.ProjectModel;
import com.chetraseng.sunrise_task_flow_api.model.TaskModel;
import com.chetraseng.sunrise_task_flow_api.model.TaskStatus;
import com.chetraseng.sunrise_task_flow_api.repository.ProjectRepository;
import com.chetraseng.sunrise_task_flow_api.repository.TaskRepository;
import com.chetraseng.sunrise_task_flow_api.spec.TaskSpec;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.awt.print.Pageable;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final TaskMapper taskMapper;

    @Override
    public List<TaskResponse> findAll() {

        return taskMapper.toTaskResponseList(
                taskRepository.findAll()
        );
    }

    @Override
    public TaskResponse findById(Long id) {

        TaskModel task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        return taskMapper.toTaskResponse(task);
    }

    @Override
    public TaskResponse create(TaskRequest request) {

        TaskModel task = new TaskModel();

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStatus(request.getStatus());
        task.setPriority(request.getPriority());
        task.setDueDate(request.getDueDate());

        if (request.getProjectId() != null) {

            ProjectModel project = projectRepository
                    .findById(request.getProjectId())
                    .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

            task.setProject(project);
        }

        TaskModel savedTask = taskRepository.save(task);

        return taskMapper.toTaskResponse(savedTask);
    }

    @Override
    public TaskResponse update(Long id, TaskRequest request) {

        TaskModel task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStatus(request.getStatus());
        task.setPriority(request.getPriority());
        task.setDueDate(request.getDueDate());

        TaskModel updated = taskRepository.save(task);

        return taskMapper.toTaskResponse(updated);
    }

    @Override
    public void delete(Long id) {

        TaskModel task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        taskRepository.deleteById(task.getId());
    }

    @Override
    public List<TaskResponse> findOverdueTasks() {
        List<TaskModel> overdueTasks = taskRepository.findOverdueTasks(LocalDate.now());
        return taskMapper.toTaskResponseList(overdueTasks);
    }

    @Override
    public PaginationResponse<TaskResponse> filterTasks(FilterTaskDto filter, Pagination pagination) {

        Specification<TaskModel> spec = TaskSpec.unrestricted();

        if (filter.getTitle() != null)
            spec = spec.and(TaskSpec.containsTitle(filter.getTitle()));

        if (filter.getProjectId() != null)
            spec = spec.and(TaskSpec.equalProjectId(filter.getProjectId()));

        if (filter.getStatus() != null)
            spec = spec.and(TaskSpec.hasStatus(filter.getStatus()));

        if (filter.getPriority() != null)
            spec = spec.and(TaskSpec.hasPriority(filter.getPriority()));

        if (filter.getDueBefore() != null)
            spec = spec.and(TaskSpec.dueBefore(filter.getDueBefore()));
        if (filter.getLabelId() != null)
            spec = spec.and(TaskSpec.hasLabel(filter.getLabelId()));

        Pageable pageable = (Pageable) PageRequest.of(
                pagination.getPage(),
                pagination.getSize(),
                Sort.by("id").descending()
        );

        Page<TaskModel> page = taskRepository.findAll(spec, (org.springframework.data.domain.Pageable) pageable);

        var data = page.getContent().stream()
                .map(taskMapper::toTaskResponse)
                .toList();

        Pagination meta = new Pagination();
        meta.setPage(pagination.getPage());
        meta.setSize(pagination.getSize());
        meta.setTotal(page.getTotalElements());
        meta.setTotalPage(page.getTotalPages());

        return new PaginationResponse<>(data, meta);
    }


    @Override
    public TaskResponse updateStatus(Long id, TaskStatus status) {
        TaskModel task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        task.setStatus(status);
        TaskModel updated = taskRepository.save(task);
        return taskMapper.toTaskResponse(updated);
    }

    @Override
    public TaskResponse addLabel(Long taskId, Long labelId) {
        TaskModel task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        TaskModel updated = taskRepository.save(task);
        return taskMapper.toTaskResponse(updated);
    }

    @Override
    public TaskResponse removeLabel(Long taskId, Long labelId) {
        TaskModel task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));


        TaskModel updated = taskRepository.save(task);
        return taskMapper.toTaskResponse(updated);
    }
}