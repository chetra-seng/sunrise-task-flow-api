package com.chetraseng.sunrise_task_flow_api.services;

import com.chetraseng.sunrise_task_flow_api.model.Task;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface TaskService {
    List<Task> findAll();
    Optional<Task> findById(Long id);
    Task create(String title, String description);
    Optional<Task> update(Long id, String title, String description);
    Optional<Task> complete(Long id);
    boolean delete(Long id);
}
