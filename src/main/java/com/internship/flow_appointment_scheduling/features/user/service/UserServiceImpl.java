package com.internship.flow_appointment_scheduling.features.user.service;

import com.internship.flow_appointment_scheduling.features.appointments.entity.Appointment;
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
import com.internship.flow_appointment_scheduling.infrastructure.exceptions.BadRequestException;
import com.internship.flow_appointment_scheduling.infrastructure.exceptions.NotFoundException;
import com.internship.flow_appointment_scheduling.infrastructure.exceptions.enums.Exceptions;
import com.internship.flow_appointment_scheduling.infrastructure.mappers.user.StaffDetailsMapper;
import com.internship.flow_appointment_scheduling.infrastructure.mappers.user.UserMapper;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;

  private final UserMapper userMapper;
  private final StaffDetailsMapper staffDetailsMapper;

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

  @Override
  public void delete(Long id) {
    User user = findById(id);

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

  @Override
  public UserView modifyStaff(Long id, StaffModifyDto dto) {
    User staff = findById(id);
    StaffDetails staffDetails = staff.getStaffDetails();

    if (UserRoles.CLIENT == staff.getRole()) {
      throw new BadRequestException(Exceptions.USER_IS_NOT_AN_STAFF);
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
