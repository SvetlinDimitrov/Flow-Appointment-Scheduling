package com.internship.flow_appointment_scheduling.features.user.repository;

import com.internship.flow_appointment_scheduling.features.user.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

  boolean existsByEmail(String email);

  Optional<User> findByEmail(String email);
}
