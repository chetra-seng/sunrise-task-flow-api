package com.chetraseng.sunrise_task_flow_api.service;

import com.chetraseng.sunrise_task_flow_api.model.ProjectModel;
import org.springframework.data.jpa.repository.JpaRepository;

interface ProjectModelRepository extends JpaRepository<ProjectModel, Long> {
}
