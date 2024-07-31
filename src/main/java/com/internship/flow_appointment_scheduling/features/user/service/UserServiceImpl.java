package com.internship.flow_appointment_scheduling.features.user.service;

import com.internship.flow_appointment_scheduling.features.user.dto.UserPostRequest;
import com.internship.flow_appointment_scheduling.features.user.dto.UserPutRequest;
import com.internship.flow_appointment_scheduling.features.user.dto.UserView;
import com.internship.flow_appointment_scheduling.features.user.entity.User;
import com.internship.flow_appointment_scheduling.features.user.repository.UserRepository;
import com.internship.flow_appointment_scheduling.infrastructure.exceptions.UserAlreadyExistsException;
import com.internship.flow_appointment_scheduling.infrastructure.exceptions.UserNotFoundException;
import com.internship.flow_appointment_scheduling.infrastructure.exceptions.enums.ExceptionMessages;
import com.internship.flow_appointment_scheduling.infrastructure.mappers.UserMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;

  public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
    this.userRepository = userRepository;
    this.userMapper = userMapper;
  }

  @Override
  public Page<UserView> getAll(Pageable pageable) {
    return userRepository.findAll(pageable)
        .map(userMapper::toView);
  }

  @Override
  public UserView getById(Long id) {
    return userRepository.findById(id)
        .map(userMapper::toView)
        .orElseThrow(() -> new UserNotFoundException(String.format(ExceptionMessages.USER_NOT_FOUND.message, id)));
  }

  @Override
  public UserView create(UserPostRequest createDto) {

    if (userRepository.existsByEmail(createDto.email()))
      throw new UserAlreadyExistsException(String.format(ExceptionMessages.USER_ALREADY_EXISTS.message, createDto.email()));

    User userToSave = userMapper.toEntity(createDto);

    return userMapper.toView(userRepository.save(userToSave));
  }

  @Override
  public UserView update(Long id, UserPutRequest putDto) {

    User entity = userRepository.findById(id)
        .orElseThrow(() -> new UserNotFoundException(String.format(ExceptionMessages.USER_NOT_FOUND.message, id)));

    userMapper.updateEntity(entity, putDto);

    return userMapper.toView(userRepository.save(entity));
  }

  @Override
  public void delete(Long id) {
    if (!userRepository.existsById(id))
      throw new UserNotFoundException(String.format(ExceptionMessages.USER_NOT_FOUND.message, id));

    userRepository.deleteById(id);
  }

}
