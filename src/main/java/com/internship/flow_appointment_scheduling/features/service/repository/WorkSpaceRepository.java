package com.internship.flow_appointment_scheduling.features.service.repository;

import com.internship.flow_appointment_scheduling.features.service.entity.WorkSpace;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkSpaceRepository extends JpaRepository<WorkSpace, Long> {

  boolean existsByName(String name);
}
