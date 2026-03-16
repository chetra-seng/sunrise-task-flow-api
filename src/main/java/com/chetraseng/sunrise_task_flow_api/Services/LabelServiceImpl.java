package com.chetraseng.sunrise_task_flow_api.Services;

import com.chetraseng.sunrise_task_flow_api.dto.LabelRequest;
import com.chetraseng.sunrise_task_flow_api.dto.LabelResponse;
import com.chetraseng.sunrise_task_flow_api.dto.TaskResponse;
import com.chetraseng.sunrise_task_flow_api.exception.ResourceNotFoundException;
import com.chetraseng.sunrise_task_flow_api.mapper.LabelMapper;
import com.chetraseng.sunrise_task_flow_api.mapper.TaskMapper;
import com.chetraseng.sunrise_task_flow_api.model.LabelModel;
import com.chetraseng.sunrise_task_flow_api.repository.LabelRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LabelServiceImpl implements LabelService {
    private final LabelRepository labelRepository;
    private final LabelMapper labelMapper;
    private final TaskMapper taskMapper;

    @Override
    public List<LabelResponse> findAll() {
        return labelRepository.findAll().stream().map(labelMapper::toLabelResponse).collect(Collectors.toList());
    }

    @Override
    public LabelResponse findById(Long id) {
        return labelRepository.findById(id).map(labelMapper::toLabelResponse).orElseThrow(()-> new ResourceNotFoundException("Label not found with ID: " + id));
    }

    @Override
    @Transactional
    public LabelResponse create(LabelRequest request) {
        LabelResponse label = labelMapper.toLabelResponse(new LabelModel());
        return labelMapper.toLabelResponse(labelRepository.save(new LabelModel()));
    }

    @Override
    public LabelResponse update(Long id, LabelRequest request) {
        return null;
    }

    @Override
    public void delete(Long id) {

    }

    @Override
    public List<TaskResponse> findTasksByLabelId(Long id) {
        LabelModel label = labelRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Label not found with id: "+id));
        return label.getTasks().stream().map(taskMapper::toTaskResponse).collect(Collectors.toList());
    }
}
