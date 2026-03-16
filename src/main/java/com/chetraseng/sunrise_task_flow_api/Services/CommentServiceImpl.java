package com.chetraseng.sunrise_task_flow_api.Services;

import com.chetraseng.sunrise_task_flow_api.dto.CommentRequest;
import com.chetraseng.sunrise_task_flow_api.dto.CommentResponse;
import com.chetraseng.sunrise_task_flow_api.exception.ResourceNotFoundException;
import com.chetraseng.sunrise_task_flow_api.mapper.CommentMapper;
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
    public List<CommentResponse> findByTaskId(Long taskId) {
        if(!taskRepository.existsById(taskId)){
            throw new ResourceNotFoundException("Task not found with id: " + taskId);
        }
        return commentRepository.findByTaskIdOrderByCreatedAtDesc(taskId).stream().map(commentMapper::toCommentResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentResponse create(Long taskId, CommentRequest request) {
        // 1. Find the parent task
        var task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + taskId));

        // 2. Map request to model and link the task
        var comment = commentMapper.toModel(request);
        comment.setTask(task);

        // 3. Save and return response
        return commentMapper.toCommentResponse(commentRepository.save(comment));
    }

    @Override
    @Transactional
    public CommentResponse update(Long id, CommentRequest request) {
        var comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + id));

        // Update fields using mapper (content, author, etc.)
        commentMapper.updateModel(comment, request);

        return commentMapper.toCommentResponse(commentRepository.save(comment));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!commentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Comment not found with id: " + id);
        }
        commentRepository.deleteById(id);
    }
}

