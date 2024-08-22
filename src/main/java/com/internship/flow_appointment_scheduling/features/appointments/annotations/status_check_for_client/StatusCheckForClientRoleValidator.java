package com.internship.flow_appointment_scheduling.features.appointments.annotations.status_check_for_client;

import com.internship.flow_appointment_scheduling.features.appointments.entity.enums.AppointmentStatus;
import com.internship.flow_appointment_scheduling.features.user.entity.enums.UserRoles;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class StatusCheckForClientRoleValidator implements
    ConstraintValidator<StatusCheckForClientRole, AppointmentStatus> {

  @Override
  public boolean isValid(AppointmentStatus status, ConstraintValidatorContext context) {
    if (status == null) {
      return true;
    }

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    boolean isTheUserClient = authentication.getAuthorities()
        .stream()
        .anyMatch(a -> a.getAuthority().equals("ROLE_" + UserRoles.CLIENT));

    if (isTheUserClient &&
        (status == AppointmentStatus.APPROVED || status == AppointmentStatus.COMPLETED)) {
      return false;
    }
    return true;
  }
}