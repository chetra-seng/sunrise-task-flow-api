package com.chetraseng.sunrise_task_flow_api.service;

import com.chetraseng.sunrise_task_flow_api.dto.CommentRequest;
import com.chetraseng.sunrise_task_flow_api.dto.CommentResponse;
import com.chetraseng.sunrise_task_flow_api.exception.ResourceNotFoundException;
import com.chetraseng.sunrise_task_flow_api.mapper.CommentMapper;
import com.chetraseng.sunrise_task_flow_api.model.CommentModel;
import com.chetraseng.sunrise_task_flow_api.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
@RequiredArgsConstructor
@Service
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    @Override
    public List<CommentResponse> findByTaskId(Long taskId) {
        return commentRepository.findById(taskId).stream().map(commentMapper::toCommentResponse).toList();
    }

    @Override
    public CommentResponse create(Long taskId, CommentRequest request) {
        CommentModel commentModel = commentRepository.findById(taskId).orElseThrow(() ->new ResourceNotFoundException("id not found" + taskId));
        commentModel.setContent(request.getContent());
        commentModel.setAuthor(request.getAuthor());
        return commentMapper.toCommentResponse(commentRepository.save(commentModel));
    }

    @Override
    public CommentResponse update(Long id, CommentRequest request) {
        CommentModel commentModel = commentRepository.findById(id).orElseThrow(() ->new ResourceNotFoundException("id not found" + id));
        commentModel.setContent(request.getContent());
        commentModel.setAuthor(request.getAuthor());
        return commentMapper.toCommentResponse(commentRepository.save(commentModel));
    }

    @Override
    public void delete(Long id) {
        if (commentRepository.existsById(id)) {
            throw new ResourceNotFoundException("id not found" + id);
        }
        commentRepository.deleteById(id);

    }
}
