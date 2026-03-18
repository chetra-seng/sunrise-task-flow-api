package com.chetraseng.sunrise_task_flow_api.repository;

import com.chetraseng.sunrise_task_flow_api.model.CommentModel;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<CommentModel,Long> {
    List<CommentModel> findByTaskIdOrderByCreatedAtDesc(Long taskId);

    long countByTaskId(Long taskId);
}
