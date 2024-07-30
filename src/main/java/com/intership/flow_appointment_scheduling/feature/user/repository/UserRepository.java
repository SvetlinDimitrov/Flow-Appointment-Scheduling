package com.intership.flow_appointment_scheduling.feature.user.repository;

import com.intership.flow_appointment_scheduling.feature.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

  boolean existsByEmail(String email);
}
