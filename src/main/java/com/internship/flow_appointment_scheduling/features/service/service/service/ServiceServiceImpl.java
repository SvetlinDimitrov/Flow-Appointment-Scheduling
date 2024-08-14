package com.internship.flow_appointment_scheduling.features.service.service.service;

import com.internship.flow_appointment_scheduling.features.service.dto.ServicePostPutRequest;
import com.internship.flow_appointment_scheduling.features.service.dto.ServiceView;
import com.internship.flow_appointment_scheduling.features.service.entity.Service;
import com.internship.flow_appointment_scheduling.features.service.repository.ServiceRepository;
import com.internship.flow_appointment_scheduling.features.service.service.work_space.WorkSpaceService;
import com.internship.flow_appointment_scheduling.features.user.entity.User;
import com.internship.flow_appointment_scheduling.features.user.service.UserService;
import com.internship.flow_appointment_scheduling.infrastructure.exceptions.ServiceNotFoundException;
import com.internship.flow_appointment_scheduling.infrastructure.mappers.ServiceMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@org.springframework.stereotype.Service
public class ServiceServiceImpl implements ServiceService {

  private final ServiceRepository serviceRepository;
  private final ServiceMapper serviceMapper;
  private final UserService userService;
  private final WorkSpaceService workSpaceService;

  public ServiceServiceImpl(ServiceRepository serviceRepository, ServiceMapper serviceMapper,
      UserService userService, WorkSpaceService workSpaceService) {
    this.serviceRepository = serviceRepository;
    this.serviceMapper = serviceMapper;
    this.userService = userService;
    this.workSpaceService = workSpaceService;
  }

  @Override
  public Page<ServiceView> getAll(Pageable pageable, String employeeEmail) {
    return Optional.ofNullable(employeeEmail)
        .map(email -> serviceRepository.findAllByUsersEmail(email, pageable))
        .orElse(serviceRepository.findAll(pageable))
        .map(serviceMapper::toView);
  }

  @Override
  public ServiceView getById(Long id) {
    return serviceMapper.toView(findById(id));
  }

  @Override
  public ServiceView assignEmployee(Long serviceId, String employeeEmail) {
    Service service = findById(serviceId);
    User user = userService.findByEmail(employeeEmail);

    service.getUsers().add(user);

    return serviceMapper.toView(serviceRepository.save(service));
  }

  @Override
  public ServiceView unassignEmployee(Long serviceId, String employeeEmail) {
    Service service = findById(serviceId);
    User user = userService.findByEmail(employeeEmail);

    service.getUsers().remove(user);

    return serviceMapper.toView(serviceRepository.save(service));
  }

  @Override
  public ServiceView create(ServicePostPutRequest createDto, String userEmail) {
    Service service = serviceMapper.toEntity(createDto);

    List<User> users = new ArrayList<>();
    users.add(userService.findByEmail(userEmail));

    service.setUsers(users);
    service.setWorkSpace(workSpaceService.findByName(createDto.workSpaceName()));

    return serviceMapper.toView(serviceRepository.save(service));
  }

  @Override
  public ServiceView update(Long id, ServicePostPutRequest putDto) {
    Service entity = findById(id);

    serviceMapper.updateEntity(entity, putDto);

    return serviceMapper.toView(serviceRepository.save(entity));
  }

  @Override
  public void delete(Long id) {
    serviceRepository.delete(findById(id));
  }

  private Service findById(Long id) {
    return serviceRepository
        .findById(id)
        .orElseThrow(() -> new ServiceNotFoundException(id));
  }
}
