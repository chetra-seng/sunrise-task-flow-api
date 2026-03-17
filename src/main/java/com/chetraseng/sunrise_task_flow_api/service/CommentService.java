package com.chetraseng.sunrise_task_flow_api.service;

import com.chetraseng.sunrise_task_flow_api.dto.CommentRequest;
import com.chetraseng.sunrise_task_flow_api.dto.CommentResponse;

import java.util.List;

public interface CommentService {
    List<CommentResponse> findByTaskId(Long taskId);
    CommentResponse create(Long taskId, CommentRequest request);
    CommentResponse update(Long id, CommentRequest request);
    void delete(Long id);
}