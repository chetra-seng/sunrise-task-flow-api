package com.chetraseng.sunrise_task_flow_api.mapper;

import com.chetraseng.sunrise_task_flow_api.dto.CommentRequest;
import com.chetraseng.sunrise_task_flow_api.dto.CommentResponse;
import com.chetraseng.sunrise_task_flow_api.model.CommentModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CommentMapper {

  @Mapping(target = "authorEmail", source = "user.email")  // Step 11.6: Add this
  CommentResponse toCommentResponse(CommentModel comment);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "task", ignore = true)
  @Mapping(target = "user", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  CommentModel toCommentModel(CommentRequest request);
}