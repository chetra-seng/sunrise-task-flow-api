package com.chetraseng.sunrise_task_flow_api.mapper;

import com.chetraseng.sunrise_task_flow_api.dto.ProjectRequest;
import com.chetraseng.sunrise_task_flow_api.dto.ProjectResponse;
import com.chetraseng.sunrise_task_flow_api.model.ProjectModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProjectMapper {
    @Mapping(target = "taskCount",
            expression = "java(project.getTasks() != null ? project.getTasks().size() : 0)")
    ProjectResponse toProjectResponse(ProjectModel project);
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tasks", ignore = true)
    ProjectModel toModel(ProjectRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tasks", ignore = true)
    void updateModel(@MappingTarget ProjectModel project, ProjectRequest request);

}
