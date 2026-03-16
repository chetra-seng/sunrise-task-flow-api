package com.chetraseng.sunrise_task_flow_api.repository;

import com.chetraseng.sunrise_task_flow_api.model.CommentModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<CommentModel, Long> {
    public List<CommentModel> findByTaskIdOrderByCreatedAtDesc(Long taskId);
    public long countByTaskId(Long taskId);
}
