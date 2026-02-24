package com.chetraseng.sunrise_task_flow_api.services;

import com.chetraseng.sunrise_task_flow_api.dto.UserInfoDto;
import com.chetraseng.sunrise_task_flow_api.mapper.UserMapper;
import com.chetraseng.sunrise_task_flow_api.model.UserModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;

  private List<UserModel> users =
      List.of(
          new UserModel(1L, "user1@gmail.com", "user 1", "123456"),
          new UserModel(2L, "user2@gmail.com", "user 2", "123kjal6"),
          new UserModel(3L, "user3@gmail.com", "user 3", "hehehemeowmeow"));

  @Override
  public List<UserInfoDto> getAllUsers() {
    return users.stream()
        .map(userMapper::toUserDto)
        .toList();
  }
}
