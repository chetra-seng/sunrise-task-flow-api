package com.chetraseng.sunrise_task_flow_api.mapper;

import com.chetraseng.sunrise_task_flow_api.dto.TaskRequest;
import com.chetraseng.sunrise_task_flow_api.dto.TaskResponse;
import com.chetraseng.sunrise_task_flow_api.model.LabelModel;
import com.chetraseng.sunrise_task_flow_api.model.TaskModel;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TaskMapper {
  @Mapping(target = "projectName", source = "project.name")
  @Mapping(target = "projectId", source = "project.id")
  @Mapping(target = "labelNames", source = "labels", qualifiedByName = "labelsToNames")
  @Mapping(target = "commentCount",expression = "java(task.getComments() != null? task.getComments().size() : 0)")
  TaskResponse toTaskResponse(TaskModel task);

  // ═══════════════════════════════════════════════════════════════════════════
  // Exercise 1: Add mappings for new TaskResponse fields
  // ═══════════════════════════════════════════════════════════════════════════

  // TODO: Add mapping for 'labelNames' — convert List<LabelModel> to List<String>
  //   Hint: Create a default method with @Named annotation:
  //     @Named("labelsToNames")
  //     default List<String> labelsToNames(List<LabelModel> labels) { ... }
  //   Then add: @Mapping(target = "labelNames", source = "labels", qualifiedByName =
  // "labelsToNames")
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "project", ignore = true)
  @Mapping(target = "labels", ignore = true)
  @Mapping(target = "comments", ignore = true)
  TaskModel toModel(TaskRequest request);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "project", ignore = true)
  @Mapping(target = "labels", ignore = true)
  @Mapping(target = "comments", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  void updateModel(@MappingTarget TaskModel task, TaskRequest request);

  @Named("labelsToNames")
  default List<String> labelsToNames(List<LabelModel> labels) {
    if (labels == null) return List.of();
    return labels.stream().map(LabelModel::getName).toList();
  }



    // TODO: Add mapping for 'commentCount'
  //   Hint: @Mapping(target = "commentCount", expression = "java(task.getComments() != null ?
  // task.getComments().size() : 0)")
}
