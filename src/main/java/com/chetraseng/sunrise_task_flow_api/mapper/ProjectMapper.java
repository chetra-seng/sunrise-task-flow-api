package com.chetraseng.sunrise_task_flow_api.mapper;

import com.chetraseng.sunrise_task_flow_api.dto.ProjectResponse;
import com.chetraseng.sunrise_task_flow_api.model.ProjectModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProjectMapper {
  @Mapping(
      target = "taskCount",
      expression = "java(project.getTasks() != null ? project.getTasks().size() : 0)")
  ProjectResponse toProjectResponse(ProjectModel project);
}
