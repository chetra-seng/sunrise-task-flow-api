package com.chetraseng.sunrise_task_flow_api.service;

import com.chetraseng.sunrise_task_flow_api.dto.*;
import com.chetraseng.sunrise_task_flow_api.exception.ResourceNotFoundException;
import com.chetraseng.sunrise_task_flow_api.mapper.TaskMapper;
import com.chetraseng.sunrise_task_flow_api.model.LabelModel;
import com.chetraseng.sunrise_task_flow_api.model.ProjectModel;
import com.chetraseng.sunrise_task_flow_api.model.TaskModel;
import com.chetraseng.sunrise_task_flow_api.model.TaskStatus;
import com.chetraseng.sunrise_task_flow_api.repository.LabelRepository;
import com.chetraseng.sunrise_task_flow_api.repository.TaskRepository;
import com.chetraseng.sunrise_task_flow_api.spec.TaskSpec;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Data
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final ProjectModelRepository projectModelRepository;
    private final LabelRepository labelRepository;


    @Override
    public List<TaskResponse> findAll() {
        List<TaskModel> tasks = taskRepository.findAll();
        return tasks.stream().map(taskMapper::toResponse).toList();

        }

    @Override
    public TaskResponse findById(Long id) {
        return taskRepository.findById(id).map(taskMapper::toResponse).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));
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
        TaskModel task = taskRepository.findById(id).orElseThrow(() -> new RuntimeException("Task not found"));
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());

        return taskMapper.toResponse(taskRepository.save(task));
    }

    @Override
    public void delete(Long id) {
        taskRepository.deleteById(id);

    }

    @Override
    public List<TaskResponse> findOverdueTasks() {
        return taskRepository.findOverdueTasks(LocalDate.now()).stream().map(taskMapper::toResponse).toList();

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
                .map(taskMapper::toResponse)
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
        TaskModel task = taskRepository.findById(id).orElseThrow(() -> new RuntimeException("Task not found" + id));
        task.setStatus(status);
        return taskMapper.toResponse(taskRepository.save(task));
    }

    @Override
    public TaskResponse addLabel(Long taskId, Long labelId) {
        TaskModel task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + taskId));

        LabelModel label = labelRepository.findById(labelId)
                .orElseThrow(() -> new ResourceNotFoundException("Label not found with id: " + labelId));

        task.getLabels().add(label);

        return taskMapper.toResponse(taskRepository.save(task));
    }


    @Override
    public TaskResponse removeLabel(Long taskId, Long labelId) {
        TaskModel task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + taskId));

        LabelModel label = labelRepository.findById(labelId)
                .orElseThrow(() -> new ResourceNotFoundException("Label not found with id: " + labelId));

        task.getLabels().remove(label);

        return taskMapper.toResponse(taskRepository.save(task));    }

}
