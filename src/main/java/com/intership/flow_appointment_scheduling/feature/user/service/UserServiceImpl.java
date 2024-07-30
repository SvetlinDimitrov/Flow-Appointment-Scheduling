package com.intership.flow_appointment_scheduling.feature.user.service;

import com.intership.flow_appointment_scheduling.feature.user.dto.UserPostRequest;
import com.intership.flow_appointment_scheduling.feature.user.dto.UserPutRequest;
import com.intership.flow_appointment_scheduling.feature.user.dto.UserView;
import com.intership.flow_appointment_scheduling.feature.user.entity.User;
import com.intership.flow_appointment_scheduling.feature.user.repository.UserRepository;
import com.intership.flow_appointment_scheduling.feature.user.validator.CustomPasswordValidator;
import com.intership.flow_appointment_scheduling.infrastructure.shared.exceptions.UserAlreadyExistsException;
import com.intership.flow_appointment_scheduling.infrastructure.shared.exceptions.UserNotFoundException;
import com.intership.flow_appointment_scheduling.infrastructure.shared.exceptions.enums.ExceptionMessages;
import com.intership.flow_appointment_scheduling.infrastructure.shared.mappers.UserMapper;
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
        .map(userMapper::toUserView);
  }

  @Override
  public UserView getById(Long id) {
    return userRepository.findById(id)
        .map(userMapper::toUserView)
        .orElseThrow(() -> new UserNotFoundException(String.format(ExceptionMessages.USER_NOT_FOUND.message, id)));
  }

  @Override
  public UserView create(UserPostRequest createDto) {

    CustomPasswordValidator.validatePassword(createDto.password());

    if (userRepository.existsByEmail(createDto.email()))
      throw new UserAlreadyExistsException(String.format(ExceptionMessages.USER_ALREADY_EXISTS.message, createDto.email()));

    User userToSave = userMapper.toUserEntity(createDto);

    return userMapper.toUserView(userRepository.save(userToSave));
  }

  @Override
  public UserView update(Long id, UserPutRequest putDto) {

    User currentUser = userRepository.findById(id)
        .orElseThrow(() -> new UserNotFoundException(String.format(ExceptionMessages.USER_NOT_FOUND.message, id)));

    User userToUpdate = userMapper.toUserEntity(putDto);
    userToUpdate.setId(currentUser.getId());
    userToUpdate.setEmail(currentUser.getEmail());
    userToUpdate.setPassword(currentUser.getPassword());

    return userMapper.toUserView(userRepository.save(userToUpdate));
  }

  @Override
  public void delete(Long id) {
    if (!userRepository.existsById(id))
      throw new UserNotFoundException(String.format(ExceptionMessages.USER_NOT_FOUND.message, id));

    userRepository.deleteById(id);
  }

}
