package com.internship.flow_appointment_scheduling.features.user.annotations.not_client_role;

import com.internship.flow_appointment_scheduling.features.user.entity.enums.UserRoles;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NotClientRoleValidator implements ConstraintValidator<NotClientRole, UserRoles> {

  @Override
  public boolean isValid(UserRoles userRole, ConstraintValidatorContext context) {
    return userRole != UserRoles.CLIENT;
  }
}
