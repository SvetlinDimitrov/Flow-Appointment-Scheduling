package com.internship.flow_appointment_scheduling.features.service.repository;

import com.internship.flow_appointment_scheduling.features.service.entity.Service;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceRepository extends JpaRepository<Service, Long> {

  Page<Service> findAllByUsersEmail(String userEmail, Pageable pageable);

  List<Service> findAllByName(String name);
}
