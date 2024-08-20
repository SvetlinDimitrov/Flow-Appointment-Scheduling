package com.internship.flow_appointment_scheduling.features.user.annotations.working_hours;

import com.internship.flow_appointment_scheduling.features.user.dto.employee_details.EmployeeDetailsDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class WorkingHoursValidator implements
    ConstraintValidator<ValidWorkingHours, EmployeeDetailsDto> {

  @Override
  public boolean isValid(EmployeeDetailsDto dto, ConstraintValidatorContext context) {
    if (dto == null) {
      return true;
    }
    return dto.beginWorkingHour().isBefore(dto.endWorkingHour());
  }
}