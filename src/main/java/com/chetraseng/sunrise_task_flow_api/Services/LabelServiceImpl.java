package com.chetraseng.sunrise_task_flow_api.Services;

import com.chetraseng.sunrise_task_flow_api.dto.LabelRequest;
import com.chetraseng.sunrise_task_flow_api.dto.LabelResponse;
import com.chetraseng.sunrise_task_flow_api.dto.TaskResponse;
import com.chetraseng.sunrise_task_flow_api.exception.ResourceNotFoundException;
import com.chetraseng.sunrise_task_flow_api.mapper.LabelMapper;
import com.chetraseng.sunrise_task_flow_api.mapper.TaskMapper;
import com.chetraseng.sunrise_task_flow_api.model.LabelModel;
import com.chetraseng.sunrise_task_flow_api.repository.LabelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LabelServiceImpl implements LabelService {

    private final LabelRepository labelRepository;
    private final TaskMapper taskMapper;
    private final LabelMapper labelMapper;

    @Override
    public List<LabelResponse> findAll() {
        return labelRepository.findAll()
                .stream()
                .map(labelMapper::toLabelResponse)
                .toList();
    }

    @Override
    public LabelResponse findById(Long id) {
        LabelModel label = labelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Label not found"));
        return labelMapper.toLabelResponse(label);
    }


    @Override
    public LabelResponse create(LabelRequest request) {
        LabelModel label = new LabelModel();
        label.setName(request.getName());
        label.setColor(request.getColor());

        LabelModel saved = labelRepository.save(label);
        return labelMapper.toLabelResponse(saved);
    }

    @Override
    public LabelResponse update(Long id, LabelRequest request) {
        LabelModel label = labelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Label not found"));

        label.setName(request.getName());
        label.setColor(request.getColor());

        LabelModel updated = labelRepository.save(label);
        return labelMapper.toLabelResponse(updated);
    }

    @Override
    public void delete(Long id) {
        LabelModel label = labelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Label not found"));

        labelRepository.delete(label);
    }

    @Override
    public List<TaskResponse> findTasksByLabelId(Long id) {
        LabelModel label = labelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Label not found"));

        return taskMapper.toTaskResponseList(label.getTasks());
    }
}
