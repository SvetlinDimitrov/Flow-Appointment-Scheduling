package com.internship.flow_appointment_scheduling.features.service.annotations.staff_or_admin;

import com.internship.flow_appointment_scheduling.features.user.entity.User;
import com.internship.flow_appointment_scheduling.features.user.entity.enums.UserRoles;
import com.internship.flow_appointment_scheduling.features.user.service.UserService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

public class StaffOrAdminValidator implements ConstraintValidator<StaffOrAdmin, String> {

  @Autowired
  private UserService userService;

  @Override
  public boolean isValid(String email, ConstraintValidatorContext context) {
    User user = userService.findByEmail(email);
    if (user == null) {
      return false;
    }
    return user.getRole().equals(UserRoles.ADMINISTRATOR) ||
        user.getRole().equals(UserRoles.EMPLOYEE);
  }
}