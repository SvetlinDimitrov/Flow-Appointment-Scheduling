package com.intership.flow_appointment_scheduling.feature.user.repository;

import com.intership.flow_appointment_scheduling.feature.user.entity.Role;
import com.intership.flow_appointment_scheduling.feature.user.entity.enums.UserRoles;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

  Optional<Role> findByName(UserRoles name);
}
