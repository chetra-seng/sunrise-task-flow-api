package com.chetraseng.sunrise_task_flow_api.services;

import java.util.List;

public interface RoleService {
    void updateUserRoles(Long userId, List<Long> roleIds);
}
