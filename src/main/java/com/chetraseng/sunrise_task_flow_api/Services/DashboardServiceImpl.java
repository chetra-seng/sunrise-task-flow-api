package com.chetraseng.sunrise_task_flow_api.Services;

import com.chetraseng.sunrise_task_flow_api.dto.DashboardResponse;
import com.chetraseng.sunrise_task_flow_api.model.TaskStatus;
import com.chetraseng.sunrise_task_flow_api.repository.ProjectRepository;
import com.chetraseng.sunrise_task_flow_api.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService{

    private TaskRepository taskRepository;
    private ProjectRepository projectRepository;

    @Override
    public DashboardResponse getSummary() {
        return DashboardResponse.builder()
                .totalTasks(taskRepository.count())
                .todoCount(taskRepository.countByStatus(TaskStatus.TODO))
                .inProgressCount(taskRepository.countByStatus(TaskStatus.IN_PROGRESS))
                .doneCount(taskRepository.countByStatus(TaskStatus.DONE))
                .overdueCount(taskRepository.findOverdueTasks(LocalDate.now()).size())
                .projectstats(projectRepository.getProjectStats()).build();
    }
}
