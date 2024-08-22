package com.internship.flow_appointment_scheduling.features.service.service.service;

import com.internship.flow_appointment_scheduling.features.service.dto.ServiceDTO;
import com.internship.flow_appointment_scheduling.features.service.dto.ServiceView;
import com.internship.flow_appointment_scheduling.features.service.entity.Service;
import com.internship.flow_appointment_scheduling.features.service.entity.WorkSpace;
import com.internship.flow_appointment_scheduling.features.service.repository.ServiceRepository;
import com.internship.flow_appointment_scheduling.features.service.service.work_space.WorkSpaceService;
import com.internship.flow_appointment_scheduling.features.user.entity.User;
import com.internship.flow_appointment_scheduling.features.user.service.UserService;
import com.internship.flow_appointment_scheduling.infrastructure.exceptions.BadRequestException;
import com.internship.flow_appointment_scheduling.infrastructure.exceptions.NotFoundException;
import com.internship.flow_appointment_scheduling.infrastructure.exceptions.enums.Exceptions;
import com.internship.flow_appointment_scheduling.infrastructure.mappers.service.ServiceMapper;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class ServiceServiceImpl implements ServiceService {

  private final ServiceRepository serviceRepository;
  private final ServiceMapper serviceMapper;
  private final UserService userService;
  private final WorkSpaceService workSpaceService;

  @Override
  public Page<ServiceView> getAll(Pageable pageable, String staffEmail) {
    return Optional.ofNullable(staffEmail)
        .map(email -> serviceRepository.findAllByUsersEmail(email, pageable))
        .orElse(serviceRepository.findAll(pageable))
        .map(serviceMapper::toView);
  }

  @Override
  public ServiceView getById(Long id) {
    return serviceMapper.toView(findById(id));
  }

  @Override
  public ServiceView assignStaff(Long serviceId, String staffEmail) {
    Service service = findById(serviceId);
    User user = userService.findByEmail(staffEmail);

    List<User> users = service.getUsers();
    if (users.contains(user)) {
      throw new BadRequestException(
          Exceptions.USER_ALREADY_ASSIGN_TO_SERVICE,
          user.getEmail(), serviceId
      );
    }
    users.add(user);

    return serviceMapper.toView(serviceRepository.save(service));
  }

  @Override
  public ServiceView unassignStaff(Long serviceId, String staffEmail) {
    Service service = findById(serviceId);
    User user = userService.findByEmail(staffEmail);

    List<User> users = service.getUsers();
    if (users.contains(user)) {
      users.remove(user);
    } else {
      throw new BadRequestException(
          Exceptions.USER_NOT_FOUND_IN_SERVICE,
          user.getEmail(), serviceId
      );
    }

    return serviceMapper.toView(serviceRepository.save(service));
  }

  @Override
  public ServiceView create(ServiceDTO createDto, String userEmail) {
    Service service = serviceMapper.toEntity(createDto);
    User user = userService.findByEmail(userEmail);
    WorkSpace workSpace = workSpaceService.findByName(createDto.workSpaceName());

    service.setUsers(List.of(user));
    service.setWorkSpace(workSpace);

    return serviceMapper.toView(serviceRepository.save(service));
  }

  @Override
  public ServiceView update(Long id, ServiceDTO putDto) {
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
        .orElseThrow(() -> new NotFoundException(
            Exceptions.SERVICE_NOT_FOUND,
            id)
        );
  }
}
