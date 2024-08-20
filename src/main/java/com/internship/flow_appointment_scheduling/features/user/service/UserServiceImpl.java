package com.internship.flow_appointment_scheduling.features.user.service;

import com.internship.flow_appointment_scheduling.features.user.dto.EmployeeHireDto;
import com.internship.flow_appointment_scheduling.features.user.dto.EmployeeModifyDto;
import com.internship.flow_appointment_scheduling.features.user.dto.UserPostRequest;
import com.internship.flow_appointment_scheduling.features.user.dto.UserPutRequest;
import com.internship.flow_appointment_scheduling.features.user.dto.UserView;
import com.internship.flow_appointment_scheduling.features.user.entity.EmployeeDetails;
import com.internship.flow_appointment_scheduling.features.user.entity.User;
import com.internship.flow_appointment_scheduling.features.user.entity.enums.UserRoles;
import com.internship.flow_appointment_scheduling.features.user.repository.UserRepository;
import com.internship.flow_appointment_scheduling.infrastructure.exceptions.BadRequestException;
import com.internship.flow_appointment_scheduling.infrastructure.exceptions.NotFoundException;
import com.internship.flow_appointment_scheduling.infrastructure.exceptions.enums.Exceptions;
import com.internship.flow_appointment_scheduling.infrastructure.mappers.user.EmployeeDetailsMapper;
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
  private final EmployeeDetailsMapper employeeDetailsMapper;

  @Override
  public Page<UserView> getAll(Pageable pageable, UserRoles userRole) {
    return Optional.ofNullable(userRole)
        .map(role -> userRepository.findAllByRole(role, pageable))
        .orElseGet(() -> userRepository.findAll(pageable))
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
  public User findByEmail(String email) {
    return userRepository.findByEmail(email)
        .orElseThrow(
            () -> new NotFoundException(
                Exceptions.USER_NOT_FOUND_BY_EMAIL,
                email)
        );
  }

  @Override
  public UserView hireEmployee(EmployeeHireDto dto) {
    User employeeToSave = userMapper.toEntity(dto.userInfo());
    EmployeeDetails employeeDetails = employeeDetailsMapper.toEntity(dto.employeeDetailsDto());

    employeeToSave.setEmployeeDetails(employeeDetails);
    employeeDetails.setUser(employeeToSave);

    return userMapper.toView(userRepository.save(employeeToSave));
  }

  @Override
  public UserView modifyEmployee(Long id, EmployeeModifyDto dto) {
    User employee = findById(id);
    EmployeeDetails employeeDetails = employee.getEmployeeDetails();

    if (employee.getRole().equals(UserRoles.CLIENT)) {
      throw new BadRequestException(Exceptions.USER_IS_NOT_AN_EMPLOYEE);
    }

    employeeDetailsMapper.updateEntity(employeeDetails, dto);
    employee.setRole(dto.userRole());

    return userMapper.toView(userRepository.save(employee));
  }

  private User findById(Long id) {
    return userRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(Exceptions.USER_NOT_FOUND, id));
  }
}
