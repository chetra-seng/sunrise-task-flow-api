package com.chetraseng.sunrise_task_flow_api.Services;

import com.chetraseng.sunrise_task_flow_api.dto.*;
import com.chetraseng.sunrise_task_flow_api.exception.ResourceNotFoundException;
import com.chetraseng.sunrise_task_flow_api.mapper.TaskMapper;
import com.chetraseng.sunrise_task_flow_api.model.LabelModel;
import com.chetraseng.sunrise_task_flow_api.model.TaskModel;
import com.chetraseng.sunrise_task_flow_api.model.TaskStatus;
import com.chetraseng.sunrise_task_flow_api.repository.LabelRepository;
import com.chetraseng.sunrise_task_flow_api.repository.ProjectRepository;
import com.chetraseng.sunrise_task_flow_api.repository.TaskRepository;
import com.chetraseng.sunrise_task_flow_api.spec.TaskSpec;
import jakarta.transaction.Transactional;
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
public  class TaskServiceImpl implements TaskService {
    private final LabelRepository labelRepository;
    private final TaskRepository taskRepository;
    private  final ProjectRepository projectRepository;
    private  final TaskMapper taskMapper;

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
        TaskModel task = taskMapper.toModel(request);
        if (request.getProjectId() != null) {
            task.setProject(projectRepository.findById(request.getProjectId()).orElse(null));
        }
        return taskMapper.toTaskResponse(taskRepository.save(task));
    }
    @Override
    @Transactional
    public TaskResponse update(Long id, TaskRequest request) {
        TaskModel task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        taskMapper.updateModel(task, request);
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
    public List<TaskResponse> findOverdueTasks() {
        return taskRepository.findOverdueTasks(LocalDate.now()).stream().map(taskMapper::toTaskResponse).toList();
    }

    @Override
    public PaginationResponse<TaskResponse> filterTasks(FilterTaskDto filter, Pagination pagination) {
        Specification<TaskModel> spec = Specification.unrestricted();

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

        Pageable pageable = PageRequest.of(pagination.getPage(), pagination.getSize(),
                Sort.by("id").descending());
        Page<TaskModel> page = taskRepository.findAll(spec, pageable);

        List<TaskResponse> data = page.getContent().stream()
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
    @Transactional
    public TaskResponse updateStatus(Long id, TaskStatus status) {
        TaskModel task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        task.setStatus(status);
        return taskMapper.toTaskResponse(taskRepository.save(task));
    }
    @Override
    @Transactional
    public TaskResponse addLabel(Long taskId, Long labelId) {
        TaskModel task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        LabelModel label = labelRepository.findById(labelId)
                .orElseThrow(() -> new ResourceNotFoundException("Label not found"));

        task.getLabels().add(label);
        return taskMapper.toTaskResponse(taskRepository.save(task));
    }
    @Override
    @Transactional
    public TaskResponse removeLabel(Long taskId, Long labelId) {
        TaskModel task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        LabelModel label = labelRepository.findById(labelId)
                .orElseThrow(() -> new ResourceNotFoundException("Label not found"));

        // Remove the label from the task's collection
        task.getLabels().remove(label);

        return taskMapper.toTaskResponse(taskRepository.save(task));
    }
}
