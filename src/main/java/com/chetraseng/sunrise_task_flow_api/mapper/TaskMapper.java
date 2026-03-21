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
  @Mapping(target = "labelNames", source = "labels", qualifiedByName = "labelsToNames")
  @Mapping(
      target = "commentCount",
      expression =
          "java(task.getComments() != null ? task.getComments().size() : 0)")
  TaskResponse toTaskResponse(TaskModel task);

  @Named("labelsToNames")
  default List<String> labelsToNames(List<LabelModel> labels) {
    if (labels == null) return List.of();
    return labels.stream().map(LabelModel::getName).toList();
  }
}
