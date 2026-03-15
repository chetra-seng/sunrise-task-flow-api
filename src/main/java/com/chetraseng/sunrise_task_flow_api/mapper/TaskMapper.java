package com.chetraseng.sunrise_task_flow_api.mapper;

import com.chetraseng.sunrise_task_flow_api.dto.TaskRequest;
import com.chetraseng.sunrise_task_flow_api.dto.TaskResponse;
import com.chetraseng.sunrise_task_flow_api.model.TaskModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TaskMapper {
//  @Mapping(target = "projectName", source = "project.name")
//  @Mapping(target = "project_id", source = "project_id")

  TaskModel toEntity(TaskRequest request);
  TaskResponse toResponse(TaskModel task);


  // ═══════════════════════════════════════════════════════════════════════════
  // Exercise 1: Add mappings for new TaskResponse fields
  // ═══════════════════════════════════════════════════════════════════════════

  // TODO: Add mapping for 'labelNames' — convert List<LabelModel> to List<String>
  //   Hint: Create a default method with @Named annotation:
  //     @Named("labelsToNames")
  //     default List<String> labelsToNames(List<LabelModel> labels) { ... }
  //   Then add: @Mapping(target = "labelNames", source = "labels", qualifiedByName =
  // "labelsToNames")

  // TODO: Add mapping for 'commentCount'
  //   Hint: @Mapping(target = "commentCount", expression = "java(task.getComments() != null ?
  // task.getComments().size() : 0)")
}
