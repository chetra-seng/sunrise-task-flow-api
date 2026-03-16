package com.chetraseng.sunrise_task_flow_api.repository;

import com.chetraseng.sunrise_task_flow_api.model.LabelModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LabelRepository extends JpaRepository<LabelModel, Long> {
    Optional<LabelModel> findByName(String name);
    boolean existsByName(String name);
}
