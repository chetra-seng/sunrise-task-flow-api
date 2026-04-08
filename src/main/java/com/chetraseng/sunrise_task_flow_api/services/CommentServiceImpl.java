package com.chetraseng.sunrise_task_flow_api.services;

import com.chetraseng.sunrise_task_flow_api.dto.CommentRequest;
import com.chetraseng.sunrise_task_flow_api.dto.CommentResponse;
import com.chetraseng.sunrise_task_flow_api.exception.ResourceNotFoundException;
import com.chetraseng.sunrise_task_flow_api.mapper.CommentMapper;
import com.chetraseng.sunrise_task_flow_api.model.CommentModel;
import com.chetraseng.sunrise_task_flow_api.model.TaskModel;
import com.chetraseng.sunrise_task_flow_api.repository.CommentRepository;
import com.chetraseng.sunrise_task_flow_api.repository.TaskRepository;
import com.chetraseng.sunrise_task_flow_api.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

  private final CommentRepository commentRepository;
  private final TaskRepository taskRepository;
  private final CommentMapper commentMapper;
  private final SecurityUtils securityUtils;

  @Override
  public List<CommentResponse> findByTaskId(Long taskId) {
    taskRepository
            .findById(taskId)
            .orElseThrow(() -> new ResourceNotFoundException("Task not found: " + taskId));

    return commentRepository.findByTaskIdOrderByCreatedAtDesc(taskId).stream()
            .map(commentMapper::toCommentResponse)
            .toList();
  }

  @Override
  public CommentResponse create(Long taskId, CommentRequest request) {
    TaskModel task =
            taskRepository
                    .findById(taskId)
                    .orElseThrow(() -> new ResourceNotFoundException("Task not found: " + taskId));

    CommentModel comment = new CommentModel();
    comment.setContent(request.getContent());
    comment.setAuthor(request.getAuthor());
    comment.setTask(task);

    securityUtils.getCurrentUser().ifPresent(comment::setUser);

    return commentMapper.toCommentResponse(commentRepository.save(comment));
  }

  @Override
  public CommentResponse update(Long id, CommentRequest request) {
    CommentModel comment =
            commentRepository
                    .findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Comment not found: " + id));

    comment.setContent(request.getContent());
    comment.setAuthor(request.getAuthor());

    return commentMapper.toCommentResponse(commentRepository.save(comment));
  }

  @Override
  public void delete(Long id) {
    commentRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Comment not found: " + id));

    commentRepository.deleteById(id);
  }
}