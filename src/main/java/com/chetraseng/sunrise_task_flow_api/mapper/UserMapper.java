package com.chetraseng.sunrise_task_flow_api.mapper;

import com.chetraseng.sunrise_task_flow_api.dto.UserInfoDto;
import com.chetraseng.sunrise_task_flow_api.model.UserModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {
    @Mapping(target = "fullName", source = "name")
    UserInfoDto toUserDto(UserModel user);
}
