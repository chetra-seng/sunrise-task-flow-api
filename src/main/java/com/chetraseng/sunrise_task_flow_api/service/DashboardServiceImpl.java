package com.chetraseng.sunrise_task_flow_api.service;

import com.chetraseng.sunrise_task_flow_api.dto.DashboardResponse;
import com.chetraseng.sunrise_task_flow_api.model.TaskStatus;
import com.chetraseng.sunrise_task_flow_api.repository.ProjectRepository;
import com.chetraseng.sunrise_task_flow_api.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;

    @Override
    public DashboardResponse getDashboard() {
        return new DashboardResponse(
                taskRepository.count(),
                taskRepository.countByStatus(TaskStatus.TODO),
                taskRepository.countByStatus(TaskStatus.IN_PROGRESS),
                taskRepository.countByStatus(TaskStatus.DONE),
                taskRepository.findOverdueTasks(LocalDate.now()).size(),
                projectRepository.getProjectStats()
        );
    }

}