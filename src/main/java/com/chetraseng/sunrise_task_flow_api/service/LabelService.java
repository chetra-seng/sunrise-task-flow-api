package com.chetraseng.sunrise_task_flow_api.service;

import com.chetraseng.sunrise_task_flow_api.dto.LabelRequest;
import com.chetraseng.sunrise_task_flow_api.dto.LabelResponse;
import com.chetraseng.sunrise_task_flow_api.dto.TaskResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface LabelService {
    List<LabelResponse> findAll();
    LabelResponse findById(Long id);
    LabelResponse create(LabelRequest request);
    LabelResponse update(Long id, LabelRequest request);
    void delete(Long id);
    List<TaskResponse> findTasksByLabelId(Long id);
}
