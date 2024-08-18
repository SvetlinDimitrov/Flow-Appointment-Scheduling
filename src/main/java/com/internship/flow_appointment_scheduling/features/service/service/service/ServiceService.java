package com.internship.flow_appointment_scheduling.features.service.service.service;

import com.internship.flow_appointment_scheduling.features.service.dto.ServiceDTO;
import com.internship.flow_appointment_scheduling.features.service.dto.ServiceView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ServiceService {

  Page<ServiceView> getAll(Pageable pageable, String employeeEmail);

  ServiceView getById(Long id);

  ServiceView assignEmployee(Long serviceId, String employeeEmail);

  ServiceView unassignEmployee(Long serviceId, String employeeEmail);

  ServiceView create(ServiceDTO createDto, String userEmail);

  ServiceView update(Long id, ServiceDTO putDto);

  void delete(Long id);

}
