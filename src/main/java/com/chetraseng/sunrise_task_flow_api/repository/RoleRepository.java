package com.chetraseng.sunrise_task_flow_api.repository;

import com.chetraseng.sunrise_task_flow_api.model.RoleModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<RoleModel, Long> {
}
