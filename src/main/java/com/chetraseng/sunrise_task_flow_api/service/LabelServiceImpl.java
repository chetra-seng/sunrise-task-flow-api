package com.chetraseng.sunrise_task_flow_api.service;

import com.chetraseng.sunrise_task_flow_api.dto.LabelRequest;
import com.chetraseng.sunrise_task_flow_api.dto.LabelResponse;
import com.chetraseng.sunrise_task_flow_api.dto.TaskResponse;
import com.chetraseng.sunrise_task_flow_api.exception.ResourceNotFoundException;
import com.chetraseng.sunrise_task_flow_api.mapper.LabelMapper;
import com.chetraseng.sunrise_task_flow_api.mapper.TaskMapper;
import com.chetraseng.sunrise_task_flow_api.model.LabelModel;
import com.chetraseng.sunrise_task_flow_api.repository.LabelRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class LabelServiceImpl implements LabelService{
    private final LabelRepository labelRepository;
    private final LabelMapper labelMapper;
    private final TaskMapper taskMapper;


    @Override
    public List<LabelResponse> findAll() {
        return labelRepository.findAll().stream()
                .map(labelMapper::toLabelResponse)
                .collect(Collectors.toList());
    }

    @Override
    public LabelResponse findById(Long id) {
        LabelModel label = labelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Label not found with id: " + id));
        return labelMapper.toLabelResponse(label);
    }

    @Override
    @Transactional
    public LabelResponse create(LabelRequest request) {
        LabelModel label = new LabelModel();
        label.setName(request.getName());
        label.setColor(request.getColor());
        return labelMapper.toLabelResponse(labelRepository.save(label));
    }

    @Override
    @Transactional
    public LabelResponse update(Long id, LabelRequest request) {
        LabelModel label = labelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Label not found with id: " + id));

        label.setName(request.getName());
        label.setColor(request.getColor());
        return labelMapper.toLabelResponse(labelRepository.save(label));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!labelRepository.existsById(id)) {
            throw new ResourceNotFoundException("Label not found with id: " + id);
        }
        labelRepository.deleteById(id);
    }

    @Override
    public List<TaskResponse> findTasksByLabelId(Long id) {
        // Step 1: Verify label exists
        LabelModel label = labelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Label not found with id: " + id));

        // Step 2: Since LabelModel has @ManyToMany, we can just call getTasks()
        return label.getTasks().stream()
                .map(taskMapper::toTaskResponse)
                .collect(Collectors.toList());
    }
}
