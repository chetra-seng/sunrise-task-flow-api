package com.chetraseng.sunrise_task_flow_api.mapper;

import com.chetraseng.sunrise_task_flow_api.dto.LabelRequest;
import com.chetraseng.sunrise_task_flow_api.dto.LabelResponse;
import com.chetraseng.sunrise_task_flow_api.model.LabelModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface LabelMapper {

    LabelResponse toLabelResponse(LabelModel label);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tasks", ignore = true)
    LabelModel toModel(LabelRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tasks", ignore = true)
    void updateModel(@MappingTarget LabelModel label, LabelRequest request);
}
