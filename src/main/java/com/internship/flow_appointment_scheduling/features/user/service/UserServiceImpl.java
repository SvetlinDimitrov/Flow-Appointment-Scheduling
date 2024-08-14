package com.internship.flow_appointment_scheduling.features.user.service;

import com.internship.flow_appointment_scheduling.features.user.dto.UserPostRequest;
import com.internship.flow_appointment_scheduling.features.user.dto.UserPutRequest;
import com.internship.flow_appointment_scheduling.features.user.dto.UserView;
import com.internship.flow_appointment_scheduling.features.user.entity.User;
import com.internship.flow_appointment_scheduling.features.user.entity.enums.UserRoles;
import com.internship.flow_appointment_scheduling.features.user.repository.UserRepository;
import com.internship.flow_appointment_scheduling.infrastructure.exceptions.users.UserNotFoundException;
import com.internship.flow_appointment_scheduling.infrastructure.mappers.UserMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final PasswordEncoder passwordEncoder;

  public UserServiceImpl(UserRepository userRepository, UserMapper userMapper,
      PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.userMapper = userMapper;
    this.passwordEncoder = passwordEncoder;
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
        .orElseThrow(() -> new UserNotFoundException(id));
  }

  @Override
  public UserView create(UserPostRequest createDto) {
    User userToSave = userMapper.toEntity(createDto);
    userToSave.setPassword(passwordEncoder.encode(userToSave.getPassword()));
    userToSave.setRole(UserRoles.CLIENT);

    return userMapper.toView(userRepository.save(userToSave));
  }

  @Override
  public UserView update(Long id, UserPutRequest putDto) {
    User entity = userRepository.findById(id)
        .orElseThrow(() -> new UserNotFoundException(id));

    userMapper.updateEntity(entity, putDto);

    return userMapper.toView(userRepository.save(entity));
  }

  @Override
  public void delete(Long id) {
    if (!userRepository.existsById(id)) {
      throw new UserNotFoundException(id);
    }

    userRepository.deleteById(id);
  }

  @Override
  public User findByEmail(String email) {
    return userRepository.findByEmail(email)
        .orElseThrow(() -> new UserNotFoundException(email));
  }
}
