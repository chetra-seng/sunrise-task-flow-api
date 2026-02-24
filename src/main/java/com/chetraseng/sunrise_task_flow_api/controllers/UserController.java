package com.chetraseng.sunrise_task_flow_api.controllers;

import com.chetraseng.sunrise_task_flow_api.dto.UserInfoDto;
import com.chetraseng.sunrise_task_flow_api.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/users")
    public List<UserInfoDto> getAllUsers() {
        return this.userService.getAllUsers();
    }
}
