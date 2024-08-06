package com.internship.flow_appointment_scheduling.features.user.annotations.adminOnly;

import com.internship.flow_appointment_scheduling.features.user.entity.enums.UserRoles;
import com.internship.flow_appointment_scheduling.infrastructure.security.dto.CustomUserDetails;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AdminOnlyValidator implements ConstraintValidator<AdminOnly, UserRoles> {

  @Override
  public boolean isValid(UserRoles value, ConstraintValidatorContext context) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication != null
        && authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
      boolean isAdmin = userDetails.getAuthorities()
          .contains(new SimpleGrantedAuthority("ROLE_" + UserRoles.ADMINISTRATOR.name()));

      if (!isAdmin && value != null) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(
                "Only administrators can set this the role field")
            .addConstraintViolation();
        return false;
      }
    }
    return true;
  }
}
