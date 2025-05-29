package com.internship.flow_appointment_scheduling.features.user.annotations.admin_filed_only;

import com.internship.flow_appointment_scheduling.features.user.entity.enums.UserRoles;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class AdminOnlyFieldValidator implements ConstraintValidator<AdminOnlyField, Object> {

  @Override
  public boolean isValid(Object value, ConstraintValidatorContext context) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !authentication.isAuthenticated()) {
      return false;
    }

    if (value == null) {
      return true;
    }

    return authentication.getAuthorities().stream()
        .anyMatch(a -> a.getAuthority().equals("ROLE_" + UserRoles.ADMINISTRATOR.name()));
  }
}
