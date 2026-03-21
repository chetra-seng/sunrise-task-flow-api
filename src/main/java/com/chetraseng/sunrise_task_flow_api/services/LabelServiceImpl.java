package com.chetraseng.sunrise_task_flow_api.services;

import com.chetraseng.sunrise_task_flow_api.dto.LabelRequest;
import com.chetraseng.sunrise_task_flow_api.dto.LabelResponse;
import com.chetraseng.sunrise_task_flow_api.dto.TaskResponse;
import com.chetraseng.sunrise_task_flow_api.exception.ResourceNotFoundException;
import com.chetraseng.sunrise_task_flow_api.mapper.LabelMapper;
import com.chetraseng.sunrise_task_flow_api.mapper.TaskMapper;
import com.chetraseng.sunrise_task_flow_api.model.LabelModel;
import com.chetraseng.sunrise_task_flow_api.model.TaskModel;
import com.chetraseng.sunrise_task_flow_api.repository.LabelRepository;
import com.chetraseng.sunrise_task_flow_api.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LabelServiceImpl implements LabelService {

  private final LabelRepository labelRepository;
  private final TaskRepository taskRepository;
  private final LabelMapper labelMapper;
  private final TaskMapper taskMapper;

  @Override
  public List<LabelResponse> findAll() {
    return labelRepository.findAll().stream().map(labelMapper::toLabelResponse).toList();
  }

  @Override
  public LabelResponse findById(Long id) {
    LabelModel label =
        labelRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Label not found: " + id));
    return labelMapper.toLabelResponse(label);
  }

  @Override
  public LabelResponse create(LabelRequest request) {
    LabelModel label = new LabelModel();
    label.setName(request.getName());
    label.setColor(request.getColor());
    return labelMapper.toLabelResponse(labelRepository.save(label));
  }

  @Override
  public LabelResponse update(Long id, LabelRequest request) {
    LabelModel label =
        labelRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Label not found: " + id));
    label.setName(request.getName());
    label.setColor(request.getColor());
    return labelMapper.toLabelResponse(labelRepository.save(label));
  }

  @Override
  public void delete(Long id) {
    LabelModel label =
        labelRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Label not found: " + id));
    // Remove this label from all tasks (owning side) to clean up join table
    List<TaskModel> tasks = new ArrayList<>(label.getTasks());
    tasks.forEach(task -> task.getLabels().remove(label));
    taskRepository.saveAll(tasks);
    labelRepository.deleteById(id);
  }

  @Override
  public List<TaskResponse> findTasksByLabelId(Long id) {
    LabelModel label =
        labelRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Label not found: " + id));
    return label.getTasks().stream().map(taskMapper::toTaskResponse).toList();
  }
}
