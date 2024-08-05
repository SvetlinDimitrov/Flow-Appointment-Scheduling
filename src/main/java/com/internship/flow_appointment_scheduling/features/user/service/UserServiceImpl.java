package com.internship.flow_appointment_scheduling.features.user.service;

import com.internship.flow_appointment_scheduling.features.user.dto.UserPostRequest;
import com.internship.flow_appointment_scheduling.features.user.dto.UserPutRequest;
import com.internship.flow_appointment_scheduling.features.user.dto.UserView;
import com.internship.flow_appointment_scheduling.features.user.entity.User;
import com.internship.flow_appointment_scheduling.features.user.entity.enums.UserRoles;
import com.internship.flow_appointment_scheduling.features.user.repository.UserRepository;
import com.internship.flow_appointment_scheduling.infrastructure.exceptions.UserNotFoundException;
import com.internship.flow_appointment_scheduling.infrastructure.mappers.UserMapper;
import com.internship.flow_appointment_scheduling.infrastructure.security.dto.UserAuth;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final PasswordEncoder passwordEncoder;

  public UserServiceImpl(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder) {
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
    UserAuth userAuth = extractUserFromAuth();

    if (userAuth.getAuthorities().contains(getRole(UserRoles.CLIENT))
        && !userAuth.getEntity().getId().equals(id))
      throw new ResponseStatusException(HttpStatus.FORBIDDEN);

    return userRepository.findById(id)
        .map(userMapper::toView)
        .orElseThrow(() -> new UserNotFoundException(id));
  }

  @Override
  public UserView create(UserPostRequest createDto) {
    User userToSave = userMapper.toEntity(createDto);
    userToSave.setPassword(passwordEncoder.encode(userToSave.getPassword()));

    return userMapper.toView(userRepository.save(userToSave));
  }

  @Override
  public UserView update(Long id, UserPutRequest putDto) {
    UserAuth userAuth = extractUserFromAuth();

    if (!userAuth.getEntity().getId().equals(id) && (
        userAuth.getAuthorities().contains(getRole(UserRoles.CLIENT)) ||
            userAuth.getAuthorities().contains(getRole(UserRoles.EMPLOYEE)))
    )
      throw new ResponseStatusException(HttpStatus.FORBIDDEN);

    User entity = userRepository.findById(id)
        .orElseThrow(() -> new UserNotFoundException(id));

    userMapper.updateEntity(entity, putDto);

    return userMapper.toView(userRepository.save(entity));
  }

  @Override
  public void delete(Long id) {
    UserAuth userAuth = extractUserFromAuth();

    if (!userAuth.getEntity().getId().equals(id) && (
        userAuth.getAuthorities().contains(getRole(UserRoles.CLIENT)) ||
            userAuth.getAuthorities().contains(getRole(UserRoles.EMPLOYEE)))
    )
      throw new ResponseStatusException(HttpStatus.FORBIDDEN);

    if (!userRepository.existsById(id))
      throw new UserNotFoundException(id);

    userRepository.deleteById(id);
  }

  private UserAuth extractUserFromAuth() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication != null && authentication.getPrincipal() instanceof UserAuth) {
      return (UserAuth) authentication.getPrincipal();
    }
    throw new ResponseStatusException(HttpStatus.FORBIDDEN);
  }

  private SimpleGrantedAuthority getRole(UserRoles role) {
    return new SimpleGrantedAuthority("ROLE_" + role.name());
  }

}
