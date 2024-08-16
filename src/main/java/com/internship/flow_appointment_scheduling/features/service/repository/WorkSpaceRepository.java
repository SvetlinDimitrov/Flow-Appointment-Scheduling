package com.internship.flow_appointment_scheduling.features.service.repository;

import com.internship.flow_appointment_scheduling.features.service.entity.WorkSpace;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkSpaceRepository extends JpaRepository<WorkSpace, Long> {

  boolean existsByName(String name);

  Optional<WorkSpace> findByName(String name);
}
