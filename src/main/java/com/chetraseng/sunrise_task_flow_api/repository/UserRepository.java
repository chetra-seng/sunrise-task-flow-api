package com.chetraseng.sunrise_task_flow_api.repository;

import com.chetraseng.sunrise_task_flow_api.model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserModel, Long> {
    Optional<UserModel> findByEmail(String email);
    boolean existsByEmail(String email);

    @Repository
    public interface userRepository extends JpaRepository<UserModel, Long> {
        Optional<UserModel> findByEmail(String email);
        boolean existsByEmail(String email);
    }
}