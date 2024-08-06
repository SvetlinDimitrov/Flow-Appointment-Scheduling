package com.internship.flow_appointment_scheduling.infrastructure.security.config;

import com.internship.flow_appointment_scheduling.features.user.entity.enums.UserRoles;
import com.internship.flow_appointment_scheduling.features.user.repository.UserRepository;
import com.internship.flow_appointment_scheduling.infrastructure.security.dto.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

@Component(value = "permissionEvaluator")
public class PermissionEvaluator {

  private final UserRepository userRepository;

  public PermissionEvaluator(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public boolean halfClientEmployeeAccess(Authentication authentication, Long id) {
    if (authentication != null
        && authentication.getPrincipal() instanceof CustomUserDetails userDetails) {

      return userRepository.findById(id)
          .filter(value -> userDetails.user().getEmail().equals(value.getEmail()) ||
              (!userDetails.getAuthorities().contains(getRole(UserRoles.CLIENT)) &&
                  !userDetails.getAuthorities().contains(getRole(UserRoles.EMPLOYEE)))
          ).isPresent();

    }
    return false;
  }

  public boolean halfClientAccess(Authentication authentication, Long id) {
    if (authentication != null
        && authentication.getPrincipal() instanceof CustomUserDetails userDetails) {

      return userRepository.findById(id)
          .filter(value -> userDetails.user().getEmail().equals(value.getEmail()) ||
              !userDetails.getAuthorities().contains(getRole(UserRoles.CLIENT))
          ).isPresent();
    }
    return false;
  }

  private SimpleGrantedAuthority getRole(UserRoles role) {
    return new SimpleGrantedAuthority("ROLE_" + role.name());
  }
}
