package com.chetraseng.sunrise_task_flow_api.Services;

import com.chetraseng.sunrise_task_flow_api.dto.CommentRequest;
import com.chetraseng.sunrise_task_flow_api.dto.CommentResponse;
import com.chetraseng.sunrise_task_flow_api.exception.ResourceNotFoundException;
import com.chetraseng.sunrise_task_flow_api.mapper.CommentMapper;
import com.chetraseng.sunrise_task_flow_api.model.CommentModel;
import com.chetraseng.sunrise_task_flow_api.repository.CommentRepository;
import com.chetraseng.sunrise_task_flow_api.repository.TaskRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final CommentMapper commentMapper;

    @Override
    public List<CommentModel> findByTaskId(Long taskId) {
        if(!taskRepository.existsById(taskId)){
            throw new ResourceNotFoundException("Task not found with id: " + taskId);
        }
        return commentRepository.findByTaskIdOrderByCreatedAtDesc(taskId).stream().map(commentMapper::toCommentResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentResponse create(Long taskId, CommentRequest request) {
        return null;
    }

    @Override
    public CommentResponse update(Long id, CommentRequest request) {
        return null;
    }

    @Override
    public void delete(Long id) {

    }
}
