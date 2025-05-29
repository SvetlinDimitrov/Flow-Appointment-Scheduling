package com.internship.flow_appointment_scheduling.features.user.repository;

import com.internship.flow_appointment_scheduling.features.user.entity.User;
import com.internship.flow_appointment_scheduling.features.user.entity.enums.UserRoles;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  Page<User> findAllByRole(UserRoles role, Pageable pageable);

  List<User> findAllByRole(UserRoles role);

  @Query("SELECT u FROM User u " + "JOIN u.services s " + "WHERE s.id = :serviceId")
  Page<User> findAllByServiceId(@Param("serviceId") Long serviceId, Pageable pageable);

  boolean existsByEmail(String email);

  Optional<User> findByEmail(String email);
}
