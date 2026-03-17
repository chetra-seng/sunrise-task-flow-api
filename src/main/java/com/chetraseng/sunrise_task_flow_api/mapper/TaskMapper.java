package com.chetraseng.sunrise_task_flow_api.mapper;

import com.chetraseng.sunrise_task_flow_api.dto.TaskResponse;
import com.chetraseng.sunrise_task_flow_api.model.LabelModel;
import com.chetraseng.sunrise_task_flow_api.model.TaskModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TaskMapper {
  @Mapping(target = "projectName", source = "project.name")
  @Mapping(target = "projectId", source = "project.id")
  TaskResponse toTaskResponse(TaskModel task);

  // ═══════════════════════════════════════════════════════════════════════════
  // Exercise 1: Add mappings for new TaskResponse fields
  // ═══════════════════════════════════════════════════════════════════════════\

  @Named("labelsToNames")
  default List<String> labelsToNames(List<LabelModel> labels) {

    if (labels == null) return List.of();

    return labels.stream()
            .map(LabelModel::getName)
            .toList();
  }

  List<TaskResponse> toTaskResponseList(List<TaskModel> tasks);

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
