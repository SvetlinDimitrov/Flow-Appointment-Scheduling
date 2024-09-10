package com.internship.flow_appointment_scheduling.features.user.service;

import com.internship.flow_appointment_scheduling.features.appointments.entity.Appointment;
import com.internship.flow_appointment_scheduling.features.appointments.entity.enums.AppointmentStatus;
import com.internship.flow_appointment_scheduling.features.appointments.repository.AppointmentRepository;
import com.internship.flow_appointment_scheduling.features.user.dto.staff_details.StaffHireDto;
import com.internship.flow_appointment_scheduling.features.user.dto.staff_details.StaffModifyDto;
import com.internship.flow_appointment_scheduling.features.user.dto.users.UserPasswordUpdate;
import com.internship.flow_appointment_scheduling.features.user.dto.users.UserPostRequest;
import com.internship.flow_appointment_scheduling.features.user.dto.users.UserPutRequest;
import com.internship.flow_appointment_scheduling.features.user.dto.users.UserView;
import com.internship.flow_appointment_scheduling.features.user.entity.StaffDetails;
import com.internship.flow_appointment_scheduling.features.user.entity.User;
import com.internship.flow_appointment_scheduling.features.user.entity.enums.UserRoles;
import com.internship.flow_appointment_scheduling.features.user.repository.UserRepository;
import com.internship.flow_appointment_scheduling.infrastructure.events.appointments.AppointmentNotificationEvent;
import com.internship.flow_appointment_scheduling.infrastructure.events.appointments.AppointmentNotificationEvent.NotificationType;
import com.internship.flow_appointment_scheduling.infrastructure.exceptions.BadRequestException;
import com.internship.flow_appointment_scheduling.infrastructure.exceptions.NotFoundException;
import com.internship.flow_appointment_scheduling.infrastructure.exceptions.enums.Exceptions;
import com.internship.flow_appointment_scheduling.infrastructure.mappers.user.StaffDetailsMapper;
import com.internship.flow_appointment_scheduling.infrastructure.mappers.user.UserMapper;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final AppointmentRepository appointmentRepository;

  private final UserMapper userMapper;
  private final StaffDetailsMapper staffDetailsMapper;

  private final ApplicationEventPublisher eventPublisher;

  @Override
  public Page<UserView> getAll(Pageable pageable, UserRoles userRole) {
    return Optional.ofNullable(userRole)
        .map(role -> userRepository.findAllByRole(role, pageable))
        .orElseGet(() -> userRepository.findAll(pageable))
        .map(userMapper::toView);
  }

  @Override
  public Page<UserView> getAllByServiceId(Pageable pageable, Long serviceId) {
    return userRepository.findAllByServiceId(serviceId, pageable)
        .map(userMapper::toView);
  }

  @Override
  public UserView getById(Long id) {
    return userMapper.toView(findById(id));
  }

  @Override
  public UserView create(UserPostRequest createDto) {

    User userToSave = userMapper.toEntity(createDto);
    return userMapper.toView(userRepository.save(userToSave));
  }

  @Override
  public UserView update(Long id, UserPutRequest putDto) {
    User entity = findById(id);

    userMapper.updateEntity(entity, putDto);

    return userMapper.toView(userRepository.save(entity));
  }

  /**
   * Deletes a user based on the provided ID.
   * <p>
   * Functionality:
   * <ul>
   *   <li>If the user is a staff member (has `staffDetails` and `staffAppointments` is not empty), send notifications to cancel the not approved and approved appointments.</li>
   * </ul>
   *
   * @param id the ID of the user to delete
   * @throws NotFoundException if the user is not found
   */
  @Override
  @Transactional
  public void delete(Long id) {
    User user = findById(id);

    user.getStaffAppointments()
        .stream()
        .filter(a -> a.getStatus() == AppointmentStatus.NOT_APPROVED ||
            a.getStatus() == AppointmentStatus.APPROVED)
        .forEach(a -> eventPublisher.publishEvent(
            new AppointmentNotificationEvent(this, a, NotificationType.CANCELED)));

    userRepository.delete(user);
  }

  @Override
  public UserView resetPassword(String email, UserPasswordUpdate dto) {
    User user = findByEmail(email);

    userMapper.updateEntity(user, dto);

    return userMapper.toView(userRepository.save(user));
  }

  @Override
  public User findByEmail(String email) {
    return userRepository.findByEmail(email)
        .orElseThrow(
            () -> new NotFoundException(
                Exceptions.USER_NOT_FOUND_BY_EMAIL,
                email)
        );
  }

  @Override
  public UserView hireStaff(StaffHireDto dto) {
    User staffToSave = userMapper.toEntity(dto.userInfo());
    StaffDetails staffDetails = staffDetailsMapper.toEntity(dto.staffDetailsDto());

    staffToSave.setRole(UserRoles.EMPLOYEE);
    staffToSave.setStaffDetails(staffDetails);
    staffDetails.setUser(staffToSave);

    return userMapper.toView(userRepository.save(staffToSave));
  }

  /**
   * Modifies the details of a staff member based on the provided ID and modification DTO.
   * <p>
   * Functionality:
   * <ul>
   *   <li>If the staff member's availability is changed from true to false, they must lose all of their appointments.</li>
   *   <li>This is because there is no date specifying how long they will be unavailable, making it impossible to check if they can handle their appointments.</li>
   *   <li>In this situation, either deny the change of availability if the staff has future appointments coming or delete all of them.</li>
   * </ul>
   *
   * @param id  the ID of the staff member to modify
   * @param dto the modification DTO containing the new details
   * @throws BadRequestException if the user is not a staff member
   */
  @Override
  @Transactional
  public UserView modifyStaff(Long id, StaffModifyDto dto) {
    User staff = findById(id);
    StaffDetails staffDetails = staff.getStaffDetails();

    if (UserRoles.CLIENT == staff.getRole()) {
      throw new BadRequestException(Exceptions.USER_IS_NOT_AN_STAFF);
    }

    if (staff.getStaffDetails().getIsAvailable().equals(true) && dto.isAvailable().equals(false)) {
      staff.getStaffAppointments()
          .stream()
          .filter(a -> a.getStatus() == AppointmentStatus.NOT_APPROVED ||
              a.getStatus() == AppointmentStatus.APPROVED)
          .forEach(a -> {
            eventPublisher.publishEvent(
                new AppointmentNotificationEvent(this, a, NotificationType.CANCELED));
            appointmentRepository.delete(a);
          });

      staff.getStaffAppointments().clear();
    }

    staffDetailsMapper.updateEntity(staffDetails, dto);

    return userMapper.toView(userRepository.save(staff));
  }

  @Override
  public void handleCompletingTheAppointment(Appointment appointment) {
    User staff = appointment.getStaff();
    StaffDetails staffDetails = staff.getStaffDetails();

    staffDetails.setProfit(staffDetails.getProfit().add(appointment.getService().getPrice()));
    staffDetails.setCompletedAppointments(staffDetails.getCompletedAppointments() + 1);

    userRepository.save(staff);
  }

  private User findById(Long id) {
    return userRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(Exceptions.USER_NOT_FOUND, id));
  }
}
