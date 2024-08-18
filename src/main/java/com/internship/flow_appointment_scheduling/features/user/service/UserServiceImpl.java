package com.internship.flow_appointment_scheduling.features.user.service;

import com.internship.flow_appointment_scheduling.features.user.dto.EmployeeDetailsDto;
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
import com.internship.flow_appointment_scheduling.infrastructure.mappers.EmployeeDetailsMapper;
import com.internship.flow_appointment_scheduling.infrastructure.mappers.UserMapper;
import java.math.BigDecimal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final UserMapper userMapper;
  private final EmployeeDetailsMapper employeeDetailsMapper;

  public UserServiceImpl(UserRepository userRepository, UserMapper userMapper,
      PasswordEncoder passwordEncoder, EmployeeDetailsMapper employeeDetailsMapper) {
    this.userRepository = userRepository;
    this.userMapper = userMapper;
    this.passwordEncoder = passwordEncoder;
    this.employeeDetailsMapper = employeeDetailsMapper;
  }

  @Override
  public Page<UserView> getAll(Pageable pageable) {
    return userRepository.findAll(pageable)
        .map(userMapper::toView);
  }

  @Override
  public UserView getById(Long id) {
    return userMapper.toView(findById(id));
  }

  @Override
  public UserView create(UserPostRequest createDto) {
    User userToSave = createUser(createDto);
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
        .orElseThrow(() -> new NotFoundException(Exceptions.USER_NOT_FOUND_BY_EMAIL, email));
  }

  @Override
  public UserView hireEmployee(EmployeeHireDto dto) {
    User employeeToSave = createUser(dto.userInfo());
    EmployeeDetails employeeDetails = createEmployeeDetails(dto.employeeDetailsDto());

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

  private User createUser(UserPostRequest createDto) {
    User userToSave = userMapper.toEntity(createDto);
    userToSave.setPassword(passwordEncoder.encode(userToSave.getPassword()));
    userToSave.setRole(UserRoles.CLIENT);
    return userToSave;
  }

  private EmployeeDetails createEmployeeDetails(EmployeeDetailsDto dto) {
    EmployeeDetails employeeDetails = employeeDetailsMapper.toEntity(dto);
    employeeDetails.setProfit(new BigDecimal(0));
    employeeDetails.setCompletedAppointments(0);
    return employeeDetails;
  }

  private User findById(Long id) {
    return userRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(Exceptions.USER_NOT_FOUND, id));
  }
}
