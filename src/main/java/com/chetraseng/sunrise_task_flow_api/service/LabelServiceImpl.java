package com.chetraseng.sunrise_task_flow_api.service;

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
    private final LabelMapper labelMapper;
    private final TaskMapper taskMapper;

    @Override
    public List<LabelResponse> findAll() {
       return labelRepository.findAll().stream().map(labelMapper::toLabelResponse).toList();
    }

    @Override
    public LabelResponse findById(Long id) {
        return labelRepository.findById(id).map(labelMapper::toLabelResponse).orElseThrow(() ->new ResourceNotFoundException("id not found" + id));
    }

    @Override
    public LabelResponse create(LabelRequest request) {
        LabelModel labelModel = new LabelModel();
        labelModel.setName(request.getName());
        labelModel.setColor(request.getColor());
        return labelMapper.toLabelResponse(labelRepository.save(labelModel));
    }

    @Override
    public LabelResponse update(Long id, LabelRequest request) {
        LabelModel labelModel = labelRepository.findById(id).orElseThrow(() ->new ResourceNotFoundException("id not found" + id));
        labelModel.setName(request.getName());
        labelModel.setColor(request.getColor());
        return labelMapper.toLabelResponse(labelRepository.save(labelModel));
    }

    @Override
    public void delete(Long id) {
        if(!labelRepository.existsById(id)){
            throw new ResourceNotFoundException("id not found" + id);
        }
        labelRepository.deleteById(id);

    }

    @Override
    public List<TaskResponse> findTasksByLabelId(Long id) {
        LabelModel label = labelRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Label not found" + id));
        return label.getTasks().stream().map(taskMapper::toResponse).toList();
    }
}
