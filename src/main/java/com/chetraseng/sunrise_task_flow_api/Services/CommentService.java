package com.chetraseng.sunrise_task_flow_api.Services;

import com.chetraseng.sunrise_task_flow_api.dto.CommentRequest;
import com.chetraseng.sunrise_task_flow_api.dto.CommentResponse;
import com.chetraseng.sunrise_task_flow_api.model.CommentModel;

import java.util.List;

public interface CommentService {
    List<CommentModel> findByTaskId(Long taskId);
    CommentResponse create(Long taskId, CommentRequest request);
    CommentResponse update(Long id, CommentRequest request);
    void delete(Long id);
}
