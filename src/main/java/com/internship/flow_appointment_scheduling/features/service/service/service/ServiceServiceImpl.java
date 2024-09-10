package com.internship.flow_appointment_scheduling.features.service.service.service;

import com.internship.flow_appointment_scheduling.features.appointments.entity.enums.AppointmentStatus;
import com.internship.flow_appointment_scheduling.features.appointments.repository.AppointmentRepository;
import com.internship.flow_appointment_scheduling.features.service.dto.ServiceDTO;
import com.internship.flow_appointment_scheduling.features.service.dto.ServiceView;
import com.internship.flow_appointment_scheduling.features.service.entity.Service;
import com.internship.flow_appointment_scheduling.features.service.entity.WorkSpace;
import com.internship.flow_appointment_scheduling.features.service.repository.ServiceRepository;
import com.internship.flow_appointment_scheduling.features.service.service.work_space.WorkSpaceService;
import com.internship.flow_appointment_scheduling.features.user.entity.User;
import com.internship.flow_appointment_scheduling.features.user.service.UserService;
import com.internship.flow_appointment_scheduling.infrastructure.events.appointments.AppointmentNotificationEvent;
import com.internship.flow_appointment_scheduling.infrastructure.events.appointments.AppointmentNotificationEvent.NotificationType;
import com.internship.flow_appointment_scheduling.infrastructure.exceptions.BadRequestException;
import com.internship.flow_appointment_scheduling.infrastructure.exceptions.NotFoundException;
import com.internship.flow_appointment_scheduling.infrastructure.exceptions.enums.Exceptions;
import com.internship.flow_appointment_scheduling.infrastructure.mappers.service.ServiceMapper;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class ServiceServiceImpl implements ServiceService {

  private final ServiceRepository serviceRepository;
  private final AppointmentRepository appointmentRepository;

  private final UserService userService;
  private final WorkSpaceService workSpaceService;

  private final ServiceMapper serviceMapper;

  private final ApplicationEventPublisher eventPublisher;

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

  /**
   * Unassigns a staff member from a service based on the provided service ID and staff email.
   * <p>
   * Functionality:
   * <ul>
   *   <li>If the staff member is being unassigned, every appointment related to this service and the staff member should be removed.</li>
   *   <li>Sends notifications to users that the appointments are canceled.</li>
   * </ul>
   *
   * @param serviceId  the ID of the service
   * @param staffEmail the email of the staff member to unassign
   * @throws NotFoundException   if the service or staff member is not found
   * @throws BadRequestException if the staff member is not assigned to the service
   */
  @Override
  @Transactional
  public ServiceView unassignStaff(Long serviceId, String staffEmail) {
    Service service = findById(serviceId);
    User user = userService.findByEmail(staffEmail);

    List<User> users = service.getUsers();
    if (users.contains(user)) {

      user.getStaffAppointments().stream()
          .filter(a -> a.getService().getId().equals(serviceId))
          .filter(a -> a.getStatus() == AppointmentStatus.NOT_APPROVED ||
              a.getStatus() == AppointmentStatus.APPROVED)
          .forEach(a -> {
            eventPublisher.publishEvent(
                new AppointmentNotificationEvent(this, a, NotificationType.CANCELED));
            appointmentRepository.delete(a);
          });

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
  public ServiceView create(ServiceDTO createDto) {
    Service service = serviceMapper.toEntity(createDto);
    WorkSpace workSpace = workSpaceService.findByName(createDto.workSpaceName());

    service.setWorkSpace(workSpace);

    return serviceMapper.toView(serviceRepository.save(service));
  }

  @Override
  public ServiceView update(Long id, ServiceDTO putDto) {
    Service entity = findById(id);
    WorkSpace workSpace = workSpaceService.findByName(putDto.workSpaceName());

    serviceMapper.updateEntity(entity, putDto);
    entity.setWorkSpace(workSpace);

    return serviceMapper.toView(serviceRepository.save(entity));
  }

  /**
   * Deletes a service based on the provided ID.
   * <p>
   * Functionality:
   * <ul>
   *   <li>Deletes the service and all associated appointments.</li>
   *   <li>Sends notifications to everyone that the appointments are canceled.</li>
   * </ul>
   *
   * @param id the ID of the service to delete
   * @throws NotFoundException if the service is not found
   */
  @Override
  public void delete(Long id) {
    Service serviceToDelete = findById(id);

    serviceToDelete.getAppointments()
        .stream()
        .filter(a -> a.getStatus() == AppointmentStatus.NOT_APPROVED ||
            a.getStatus() == AppointmentStatus.APPROVED)
        .forEach(a -> eventPublisher.publishEvent(
            new AppointmentNotificationEvent(this, a, NotificationType.CANCELED))
        );

    serviceRepository.delete(serviceToDelete);
  }

  public Service findById(Long id) {
    return serviceRepository
        .findById(id)
        .orElseThrow(() -> new NotFoundException(
            Exceptions.SERVICE_NOT_FOUND,
            id)
        );
  }
}
