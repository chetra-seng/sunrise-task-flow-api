package com.chetraseng.sunrise_task_flow_api.controllers;

import com.chetraseng.sunrise_task_flow_api.dto.request.UserRoleRequest;
import com.chetraseng.sunrise_task_flow_api.services.RoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(RoleController.BASE_URL)
@RequiredArgsConstructor
public class RoleController {
  public static final String BASE_URL = "/api/roles";
  private final RoleService roleService;

  @PutMapping("/users")
  public ResponseEntity<Void> updateUserRoles(@Valid @RequestBody UserRoleRequest request) {
    roleService.updateUserRoles(request.getUserId(), request.getRoleIds());
    return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
  }
}
