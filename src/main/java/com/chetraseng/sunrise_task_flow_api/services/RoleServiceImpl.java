package com.chetraseng.sunrise_task_flow_api.services;

import com.chetraseng.sunrise_task_flow_api.exception.ResourceNotFoundException;
import com.chetraseng.sunrise_task_flow_api.model.RoleModel;
import com.chetraseng.sunrise_task_flow_api.model.UserModel;
import com.chetraseng.sunrise_task_flow_api.model.UserRoleModel;
import com.chetraseng.sunrise_task_flow_api.repository.RoleRepository;
import com.chetraseng.sunrise_task_flow_api.repository.UserRepository;
import com.chetraseng.sunrise_task_flow_api.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final UserRoleRepository userRoleRepository;

  @Override
  public void updateUserRoles(Long userId, List<Long> roleIds) {
    UserModel user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("user not found"));

    // Find all roles and make sure they are all valid
    List<RoleModel> roles = new ArrayList<>();
    roleIds.forEach(
        (id) -> {
          RoleModel role =
              roleRepository
                  .findById(id)
                  .orElseThrow(() -> new ResourceNotFoundException("role not found"));
          roles.add(role);
        });

    // Clean up all roles
    List<UserRoleModel> oldUserRoles = userRoleRepository.findAllByUserId(userId);
    userRoleRepository.deleteAllInBatch(oldUserRoles);

    // Construct new user role
    List<UserRoleModel> newUserRoles =
        roles.stream().map(role -> UserRoleModel.builder().user(user).role(role).build()).toList();

    // save all roles
    userRoleRepository.saveAll(newUserRoles);
  }
}
