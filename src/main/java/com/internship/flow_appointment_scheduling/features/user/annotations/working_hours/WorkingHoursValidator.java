package com.internship.flow_appointment_scheduling.features.user.annotations.working_hours;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class WorkingHoursValidator implements
    ConstraintValidator<ValidWorkingHours, WorkingHours> {

  @Override
  public boolean isValid(WorkingHours dto, ConstraintValidatorContext context) {
    if (dto == null) {
      return true;
    }
    return dto.getBeginWorkingHour().isBefore(dto.getEndWorkingHour());
  }
}